#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Created on Fri May  5 09:19:31 2017

@author: ofayans
"""

import random
import uuid
import string
import pycountry
import yaml
from datetime import date
import sqlalchemy
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column
from sqlalchemy import Integer
from sqlalchemy import String
from sqlalchemy import Boolean
from sqlalchemy import Date
from sqlalchemy.orm import sessionmaker


Base = declarative_base()


def load_config(path):
    with open(path, 'r') as f:
        return yaml.load(f.read())


class Generator(object):
    def __init__(self, config):
        self.engine = sqlalchemy.create_engine(config['db_url'])
        DBSession = sessionmaker(bind=self.engine)
        self.session = DBSession()
        self.num_users = config['users']*config['multiplied_by']
        self.num_addresses = config['addresses']*config['multiplied_by']
        self.num_applications = config['applications']*config['multiplied_by']
        self.num_application_versions = config['application_versions']*config['multiplied_by']
        self.num_commercial_subscriptions = config['commercial_subscriptions']*config['multiplied_by']
        self.num_customers = config['customers']*config['multiplied_by']
        self.num_customer_properties = config['customer_properties']*config['multiplied_by']
        self.num_customer_users = config['customer_users']*config['multiplied_by']
        self.num_partners = config['partners']*config['multiplied_by']
        self.num_partner_users = config['partner_users']*config['multiplied_by']
        self.num_properties = config['properties']*config['multiplied_by']
        self.num_property_sets = config['property_sets']*config['multiplied_by']
        self.num_property_propertysets = config['property_propertysets']*config['multiplied_by']
        self.num_roles = config['roles']*config['multiplied_by']
        self.num_user_customer_roles = config['user_customer_roles']*config['multiplied_by']
        self.num_user_properties = config['user_properties']*config['multiplied_by']
        self.num_user_propertysets = config['user_propertysets']*config['multiplied_by']
        self.country_codes = [str(i.alpha_2) for i in list(pycountry.countries)]
        Base.metadata.create_all(self.engine)
        self.max_data_len = 1000

        self.find_app_versions_string = """
        select u.id, av.id from public.User u inner join ApplicationVersion av on
        av.application_id = (select cs.application_id from
        CommercialSubscription cs where customer_id in (select customer_id
                from User_Customer where user_id = u.id) and property_id in
        (select property_id from User_Property where user_id = u.id) and
        (select true from Application where id = cs.application_id and is_internal
                = true) limit 1) and av.is_non_commercial = false limit 10;
        """

    def pregenerate_ids(self):
        self.user_ids = [self.genuuid() for _ in range(self.num_users)]
        self.address_ids = [self.genuuid() for _ in range(self.num_addresses)]
        self.application_ids = [self.genuuid() for _ in range(self.num_applications)]
        self.app_version_ids = [self.genuuid() for _ in range(self.num_application_versions)]
        self.com_subscr_ids = [self.genuuid() for _ in range(self.num_commercial_subscriptions)]
        self.customer_ids = [self.genuuid() for _ in range(self.num_customers)]
        self.customer_property_ids = [self.genuuid() for _ in range(self.num_customer_properties)]
        self.customer_user_ids = [self.genuuid() for _ in range(self.num_customer_users)]
        self.partner_ids = [self.genuuid() for _ in range(self.num_partners)]
        self.partner_user_relation_ids = [self.genuuid() for _ in range(self.num_partner_users)]
        self.property_ids = [self.genuuid() for _ in range(self.num_properties)]
        self.propertyset_ids = [self.genuuid() for _ in range(self.num_property_sets)]
        self.propertyset_property_ids = [self.genuuid() for _ in range(self.num_property_propertysets)]
        self.role_ids = [self.genuuid() for _ in range(self.num_roles)]
        self.user_customer_role_ids = [self.genuuid() for _ in range(self.num_user_customer_roles)]
        self.user_type_partner_ids = [self.user_ids.pop() for _ in range(self.num_partner_users)]
        self.user_property_ids = [self.genuuid() for _ in range(self.num_user_properties)]
        self.user_propertyset_ids = [self.genuuid() for _ in range(self.num_user_propertysets)]
        self.snapshot_user_id = self.genuuid()

        # Trinities - are all possible combinations of customer_id application_id and property_id.
        # These should be unique per each commercial subscription
        self.cs_trinities = self.gen_trinities(self.customer_ids, self.property_ids, self.application_ids, self.num_commercial_subscriptions)
        # The same applies to couples - unique combinations of, for example,
        # customer and user to generate customer_user relations
        self.customer_property_combinations = self.gen_couples(self.customer_ids, self.property_ids, self.num_customer_properties)
        self.customer_user_combinations = self.gen_couples(self.customer_ids, self.user_ids, self.num_customer_users)
        self.partner_user_combinations = self.gen_couples(self.partner_ids, self.user_type_partner_ids, self.num_partner_users)
        self.property_propertyset_combinations = self.gen_couples(self.property_ids, self.propertyset_ids, self.num_property_propertysets)
        self.user_customer_role_combinations = self.gen_couples(self.customer_user_ids, self.role_ids, self.num_user_customer_roles)
        self.user_property_combinations = self.gen_couples(self.user_ids, self.property_ids, self.num_user_properties)
        self.user_propertyset_combinations = self.gen_couples(self.user_ids, self.propertyset_ids, self.num_user_propertysets)

    def gen_trinities(self, seq1, seq2, seq3, when_to_stop):
        result = []
        counter = 0
        while counter < when_to_stop:
            trinity = (random.choice(seq1), random.choice(seq2), random.choice(seq3))
            if trinity in result:
                next
            else:
                result.append(trinity)
                counter += 1
        return result

    def gen_couples(self, seq1, seq2, when_to_stop):
        result = []
        counter = 0
        while counter < when_to_stop:
            couple = (random.choice(seq1), random.choice(seq2))
            if couple in result:
                next
            else:
                result.append(couple)
                counter += 1
        return result

    def push_data(self, data):
        """
        If you try to commit too big a dataset to MySQL, it closes the
        connection, because it is a complete piece of shit. So we need to split
        big datasets to subsets not bigger than, say, 1000 elements in each
        and then feed them to MySQL one by one.
        """
        chunks = [data[x:x+self.max_data_len] for x in xrange(0, len(data), self.max_data_len)]
        for i in chunks:
            self.session.add_all(i)
            self.session.commit()

    def grant_permissions_to_apps(self):
        self.engine.execute("delete from applicationpermission;")
        self.engine.execute("""insert into applicationpermission (application_id,
        platform_operation_id) select a.id, r.id from Application a left join
        platformoperation r on r.id is not null;""")

    def doyourjob(self):
        self.addresses = [self.generate_address(i) for i in self.address_ids]
        self.customers = [self.generate_customer(i) for i in self.customer_ids]
        self.partners = [self.generate_partner(i) for i in self.partner_ids]
        self.applications = [self.generate_application(i) for i in self.application_ids]
        self.app_versions = [self.generate_app_version(i) for i in self.app_version_ids]
        self.properties = [self.generate_property(i) for i in self.property_ids]
        self.property_sets = [self.generate_property_set(i) for i in self.propertyset_ids]
        self.commercial_subscriptions = self.generate_commercial_subscriptions()
        # Number of entries in customer_hierarchy_path exactly corresponds to the number of customers
        # And contains customer-customer pairs of the same customer id, like ('15efef49-bd99-41b7-911b-8b64e4adc1c4', '15efef49-bd99-41b7-911b-8b64e4adc1c4')
        self.cust_hpaths = [self.generate_customer_hpath(i) for i in self.customer_ids]
        # The same applies to propertyset_herarchy_path
        self.propset_hpaths = [self.generate_propset_hpath(i) for i in self.propertyset_ids]
        self.users_type_customer = [self.generate_user(i, 'CUSTOMER') for i in self.user_ids]
        self.users_type_partner = [self.generate_user(i, 'PARTNER') for i in self.user_type_partner_ids]
        self.users_type_snapshot = [self.generate_user(self.snapshot_user_id, 'SNAPSHOT')]
        self.users = self.users_type_snapshot + self.users_type_customer + self.users_type_partner
        self.roles = [self.generate_role(i) for i in self.role_ids]
        self.cust_properties = [self.generate_customer_property(i, *self.customer_property_combinations.pop()) for i in self.customer_property_ids]
        self.partner_user_relations = [self.generate_partner_user(i, *self.partner_user_combinations.pop()) for i in self.partner_user_relation_ids]
        self.customer_users = [self.generate_customer_user(i, *self.customer_user_combinations.pop()) for i in self.customer_user_ids]
        self.propertyset_properties = [self.generate_prop_propset(i, *self.property_propertyset_combinations.pop()) for i in self.propertyset_property_ids]
        self.user_customer_roles = [self.generate_user_customer_role(i, *self.user_customer_role_combinations.pop()) for i in self.user_customer_role_ids]
        self.user_properties = [self.generate_user_property(i, *self.user_property_combinations.pop()) for i in self.user_property_ids]
        self.user_propertysets = [self.generate_user_property_set(i, *self.user_propertyset_combinations.pop()) for i in self.user_propertyset_ids]
        self.push_data(self.addresses)
        self.push_data(self.customers)
        self.push_data(self.partners)
        self.push_data(self.applications)
        self.push_data(self.app_versions)
        self.push_data(self.properties)
        self.push_data(self.property_sets)
        self.push_data(self.users)
        self.push_data(self.roles)
        self.push_data(self.commercial_subscriptions)
        self.push_data(self.propset_hpaths)
        self.push_data(self.cust_hpaths)
        self.push_data(self.cust_properties)
        self.push_data(self.partner_user_relations)
        self.push_data(self.customer_users)
        self.push_data(self.propertyset_properties)
        self.push_data(self.user_customer_roles)
        self.push_data(self.user_properties)
        self.push_data(self.user_propertysets)
        self.session.commit()
        self.grant_permissions_to_apps()

    def gen_etag(self):
        return self.genuuid().replace('-', '')

    def gen_randstr(self, num):
        return ''.join(random.choice(string.ascii_lowercase + string.digits) for _ in range(num))

    def gen_randnum(self, num):
        return ''.join(random.choice(string.digits) for _ in range(num))

    def genuuid(self):
        return str(uuid.uuid4())

    def gen_email(self):
        return "".join((self.gen_randstr(10), "@",
                        self.gen_randstr(10), ".com"))

    def gen_website(self):
        return "".join(("http://www.", self.gen_randstr(15), ".com"))

    def generate_address(self, provided_id):
        return Address(id=provided_id,
                       line1=self.gen_randstr(5),
                       line2=self.gen_randstr(4),
                       city=self.gen_randstr(5),
                       zip_code=self.gen_randnum(6),
                       country_code=random.choice(self.country_codes),
                       region=None
                       )

    def generate_customer(self, provided_id):
        return Customer(id=provided_id,
                        parent_id=None,
                        type='HOTEL',
                        hospitality_id=None,
                        salesforce_id=self.gen_randstr(15),
                        name=self.gen_randstr(10),
                        code=self.gen_randstr(10),
                        phone=None,
                        email=self.gen_email(),
                        website=None,
                        vat_id=None,
                        is_demo=random.choice((True, False)),
                        notes=None,
                        address_id=self.address_ids.pop(),
                        timezone='GMT',
                        is_active=True,
                        version=self.gen_etag()
                        )

    def generate_application(self, provided_id):
        return Application(id=provided_id,
                           name=self.gen_randstr(10),
                           description=self.gen_randstr(10),
                           website=self.gen_website(),
                           partner_id=random.choice(self.partner_ids),
                           is_internal=random.choice((True, False)),
                           is_active=True,
                           version=self.gen_etag()
                           )

    def generate_app_version(self, provided_id):
        return Application_Version(id=provided_id,
                                   application_id=random.choice(self.application_ids),
                                   api_manager_id=self.gen_randstr(5),
                                   name=self.gen_randstr(5),
                                   status='CERTIFIED',
                                   release_date=None,
                                   description=None,
                                   is_non_commercial=random.choice((True,
                                                                    False)),
                                   is_active=True,
                                   version=self.gen_etag()
                                   )

    def _generate_commercial_subscription(self, provided_id, customer_id,
                                          property_id, application_id):
        return Commercial_Subscription(id=provided_id,
                                       customer_id=customer_id,
                                       property_id=property_id,
                                       application_id=application_id,
                                       is_active=True,
                                       version=self.gen_etag()
                                       )

    def generate_commercial_subscriptions(self):
        result = []
        for _ in range(self.num_commercial_subscriptions):
            result.append(self._generate_commercial_subscription(
                              self.com_subscr_ids.pop(),
                              *self.cs_trinities.pop()
                              )
                          )
        return result

    def generate_customer_hpath(self, provided_id):
        return Customer_Hierarchy_Path(parent_id=provided_id,
                                       child_id=provided_id)

    def generate_propset_hpath(self, provided_id):
        return PropertySet_Hierarchy_Path(parent_id=provided_id,
                                          child_id=provided_id)

    def generate_customer_property(self, provided_id, customer_id, property_id):
        return Customer_Property(id=provided_id,
                                 customer_id=customer_id,
                                 property_id=property_id,
                                 type=random.choice(('OWNER', 'DATA_OWNER')),
                                 valid_from=str(date(2017, 1, 1)),
                                 valid_to=str(date(2030, 1, 1)),
                                 is_active=True,
                                 version=self.gen_etag()
                                 )

    def generate_customer_user(self, provided_id, customer_id, user_id):
        return Customer_User(id=provided_id,
                             customer_id=customer_id,
                             user_id=user_id,
                             is_primary=True,
                             is_active=True,
                             version=self.gen_etag()
                             )

    def generate_partner(self, provided_id):
        return Partner(id=provided_id,
                       name=self.gen_randstr(10),
                       email=self.gen_email(),
                       notes=self.gen_randstr(20),
                       website=self.gen_website(),
                       vat_id=None,
                       is_active=True,
                       version=self.gen_etag()
                       )

    def generate_partner_user(self, provided_id, partner_id, user_id):
        return Partner_User(id=provided_id,
                            partner_id=partner_id,
                            user_id=user_id,
                            is_active=True,
                            version=self.gen_etag()
                            )

    def generate_property(self, provided_id):
        return Property(id=provided_id,
                        name=self.gen_randstr(12),
                        hospitality_id=None,
                        salesforce_id=None,
                        code=self.gen_randstr(12),
                        website=self.gen_website(),
                        email=self.gen_email(),
                        is_demo=random.choice((True, False)),
                        timezone='GMT',
                        address_id=self.address_ids.pop(),
                        tti_id=None,
                        customer_id=random.choice(self.customer_ids),
                        description=None,
                        is_active=True,
                        version=self.gen_etag()
                        )

    def generate_prop_propset(self, provided_id, property_id, propertyset_id):
        return Property_PropertySet(id=provided_id,
                                    property_id=property_id,
                                    property_set_id=propertyset_id,
                                    is_active=random.choice((True, False)),
                                    version=self.gen_etag()
                                    )

    def generate_property_set(self, provided_id):
        return Property_Set(id=provided_id,
                            parent_id=None,
                            customer_id=random.choice(self.customer_ids),
                            type=random.choice(('BRAND', 'HOTEL_TYPE')),
                            name=self.gen_randstr(8),
                            description=None,
                            is_active=True,
                            version=self.gen_etag()
                            )

    def generate_role(self, provided_id):
        return Role(id=provided_id,
                    application_id=random.choice(self.application_ids),
                    type=random.choice(('CUSTOMER', 'PROPERTY')),
                    name=self.gen_randstr(8),
                    description=None,
                    is_initial=random.choice((True, False)),
                    is_active=True,
                    version=self.gen_etag()
                    )

    def generate_user(self, provided_id, user_type):
        return User(id=provided_id,
                    username=self.gen_randstr(10),
                    type=user_type,
                    first_name=self.gen_randstr(8),
                    last_name=self.gen_randstr(8),
                    phone=None,
                    timezone='GMT',
                    language_code='en-US',
                    password=None,
                    email=self.gen_email(),
                    salesforce_id=None,
                    picture=None,
                    comment=self.gen_randstr(8),
                    is_active=True,
                    version=self.gen_etag()
                    )

    def generate_user_customer_role(self, provided_id, user_customer_id, role_id):
        return User_Customer_Role(id=provided_id,
                                  user_customer_id=user_customer_id,
                                  role_id=role_id,
                                  version=self.gen_etag()
                                  )

    def generate_user_property(self, provided_id, user_id, property_id):
        return User_Property(id=provided_id, 
                             user_id=user_id,
                             property_id=property_id,
                             is_active=True,
                             version=self.gen_etag()
                             )

    def generate_user_property_set(self, provided_id, user_id, propertyset_id):
        return User_PropertySet(id=provided_id,
                                user_id=user_id,
                                property_set_id=propertyset_id,
                                is_active=True,
                                version=self.gen_etag()
                                )


class Address(Base):
    __tablename__ = "address"
    id = Column(String(36), primary_key=True)
    line1 = Column(String(500))
    line2 = Column(String(500))
    city = Column(String(250))
    zip_code = Column(String(100))
    country_code = Column(String(100))
    region = Column(String(100))


class Application(Base):
    __tablename__ = "application"
    id = Column(String(36), primary_key=True)
    name = Column(String(190))
    description = Column(String(500))
    website = Column(String(255))
    partner_id = Column(String(36))
    is_internal = Column(Boolean)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Application_Version(Base):
    __tablename__ = "applicationversion"
    id = Column(String(36), primary_key=True)
    application_id = Column(String(36))
    api_manager_id = Column(String(255))
    name = Column(String(190))
    status = Column(String(64))
    release_date = Column(Date)
    description = Column(String(500))
    is_non_commercial = Column(Boolean)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Commercial_Subscription(Base):
    __tablename__ = "commercialsubscription"
    id = Column(String(36), primary_key=True)
    customer_id = Column(String(36))
    property_id = Column(String(36))
    application_id = Column(String(36))
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Customer(Base):
    __tablename__ = "customer"
    id = Column(String(36), primary_key=True)
    parent_id = Column(String(36))
    type = Column(String(40))
    hospitality_id = Column(String(36))
    salesforce_id = Column(String(18))
    name = Column(String(255))
    code = Column(String(50))
    phone = Column(String(255))
    email = Column(String(254))
    website = Column(String(255))
    vat_id = Column(String(100))
    is_demo = Column(Boolean)
    notes = Column(String(1000))
    address_id = Column(String(36))
    timezone = Column(String(50))
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Customer_Hierarchy_Path(Base):
    __tablename__ = "customerhierarchypath"
    parent_id = Column(String(36), primary_key=True)
    child_id = Column(String(36), primary_key=True)


class Customer_Property(Base):
    __tablename__ = "customer_property"
    id = Column(String(36), primary_key=True)
    customer_id = Column(String(36))
    property_id = Column(String(36))
    type = Column(String(20))
    valid_from = Column(Date)
    valid_to = Column(Date)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Customer_User(Base):
    __tablename__ = "user_customer"
    id = Column(String(36), primary_key=True)
    customer_id = Column(String(36), primary_key=True)
    user_id = Column(String(36), primary_key=True)
    is_primary = Column(Boolean)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Partner(Base):
    __tablename__ = "partner"
    id = Column(String(36), primary_key=True)
    name = Column(String(255))
    email = Column(String(254))
    notes = Column(String(1000))
    website = Column(String(255))
    vat_id = Column(String(100))
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Partner_User(Base):
    __tablename__ = "user_partner"
    id = Column(String(36), primary_key=True)
    partner_id = Column(String(36), primary_key=True)
    user_id = Column(String(36), primary_key=True)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Property(Base):
    __tablename__ = "property"
    id = Column(String(36), primary_key=True)
    name = Column(String(255))
    hospitality_id = Column(String(36))
    salesforce_id = Column(String(18))
    code = Column(String(50))
    website = Column(String(255))
    email = Column(String(254))
    is_demo = Column(Boolean)
    timezone = Column(String(50))
    address_id = Column(String(36))
    tti_id = Column(Integer)
    customer_id = Column(String(36))
    description = Column(String(500))
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class PropertySet_Hierarchy_Path(Base):
    __tablename__ = "propertysethierarchypath"
    parent_id = Column(String(36), primary_key=True)
    child_id = Column(String(36), primary_key=True)


class Property_PropertySet(Base):
    __tablename__ = "propertyset_property"
    id = Column(String(36), primary_key=True)
    property_id = Column(String(36), primary_key=True)
    property_set_id = Column(String(36), primary_key=True)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Property_Set(Base):
    __tablename__ = "propertyset"
    id = Column(String(36), primary_key=True)
    parent_id = Column(String(36))
    customer_id = Column(String(36))
    type = Column(String(20))
    name = Column(String(255))
    description = Column(String(500))
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class Role(Base):
    __tablename__ = "role"
    id = Column(String(36), primary_key=True)
    application_id = Column(String(36))
    type = Column(String(20))
    name = Column(String(50))
    description = Column(String(500))
    is_initial = Column(Boolean)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class User(Base):
    __tablename__ = "user"
    id = Column(String(36), primary_key=True)
    username = Column(String(150))
    type = Column(String(20))
    first_name = Column(String(255))
    last_name = Column(String(255))
    phone = Column(String(255))
    timezone = Column(String(50))
    language_code = Column(String(50))
    password = Column(String(128))
    email = Column(String(254))
    salesforce_id = Column(String(64))
    picture = Column(String(500))
    comment = Column(String(500))
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class User_Customer_Role(Base):
    __tablename__ = "user_customer_role"
    id = Column(String(36), primary_key=True)
    user_customer_id = Column(String(36), primary_key=True)
    role_id = Column(String(36), primary_key=True)
    version = Column(String(32))


class User_Property(Base):
    __tablename__ = "user_property"
    id = Column(String(36), primary_key=True)
    user_id = Column(String(36), primary_key=True)
    property_id = Column(String(36), primary_key=True)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


class User_PropertySet(Base):
    __tablename__ = "user_propertyset"
    id = Column(String(36), primary_key=True)
    user_id = Column(String(36), primary_key=True)
    property_set_id = Column(String(36), primary_key=True)
    is_active = Column(Boolean, default=True)
    version = Column(String(32))


if __name__ == '__main__':
    generator = Generator(load_config('config.yaml'))
    generator.pregenerate_ids()
    generator.doyourjob()
    res = generator.engine.execute(generator.find_app_versions_string)
    user_app_version = []
    for row in res:
        user_app_version.append(row)

    with open("test_data.txt", "w") as f:
        f.write("snapshot_user_id: %s\n" % generator.snapshot_user_id)
        f.write("""Random selection of users type customers and corresponding
commercial application versions they have access to:\nuser_id                              | app_version_id\n""")
        for i in user_app_version:
            f.write("%s | %s\n" % (str(i[0]), str(i[1])))

package travel.snapshot.qa.manager.mongodb;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManagerException;
import travel.snapshot.qa.manager.mongodb.impl.MongoDBManagerImpl;

@Category(UnitTest.class)
public class MongoDBTestCase {

    @Mock
    private MongoClient mongoClient;

    @Spy
    private MongoDBManager mongoDBManager = new MongoDBManagerImpl();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void failedClosingOfClientThrowsExceptionTest() {
        expectedException.expect(MongoDBManagerException.class);
        expectedException.expectMessage("Unable to close MongoDB client: exception thrown intentionally");
        expectedException.expectCause(is(instanceOf(RuntimeException.class)));

        Mockito.doReturn(mongoClient).when(mongoDBManager).getClient();
        Mockito.doThrow(new RuntimeException("exception thrown intentionally")).when(mongoClient).close();

        mongoDBManager.closeClient();
    }
}

Feature: send messages

  Scenario: Customer object is created and event is sent
    Given Messaging server is accessible on url "localhost:61616"
    When Customer is created with random address
      | companyName           | email          | code | salesforceId           | vatId      | isDemoCustomer | phone         | website                    |
      | Creation test company | s1@tenants.biz | s1t  | salesforceid_created_1 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel |
    Then message was recieved from queue "customer_created_queue"
    And message contains text "aksajdkfjaskfj"
    And message has size 100


  Scenario: Asynchronous message Confirmation


    Given the test is connected to the JMS <server>
    And I create a simple Application to Receive the input of the topic <topic>
    And the receiver is configured to process the output.
    And a collection of Trades is loaded from the <trader> sheet

    When The Trades are sent
    And we wait until all trades have been processed

    Then the trade sent and the trade received should be equal
    And the trade received should be executed


|	server				|	trader			|	topic 	|
|tcp://127.0.0.1:61616	| testTrades.xlsx	|	MYtopic	|


Scenario: Unsubscribing
	Given the test is connected to the JMS <server>
    And I create a simple Application to Receive the input of the topic <topic>
    And the receiver is configured to process the output.
    When a Trade witch message content "STOP" is sent
    And we wait until the trade have been processed
    Then the consumer should consume the message
    And the consumer should close the connection to the server
    
|	server				|	trader			|	topic 	|
|tcp://127.0.0.1:61616	| testTrades.xlsx	|	MYtopic	|
    
    
Scenario: no old messages are processed
	Given the test is connected to the JMS <server>
    And I am starting only the cunsumer application
    Then the cunsumer should not receive any message
    Then I run the sender application
    Then the consumer should receive a message
    
|	server				|	trader			|	topic 	|
|tcp://127.0.0.1:61616	| testTrades.xlsx	|	MYtopic	|
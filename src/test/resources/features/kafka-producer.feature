Feature: Kafka message publishing
  As a user of the Kafka producer API
  I want to publish messages through the REST endpoint
  So that messages are sent to Kafka topics

  Scenario: Successfully publish a message
    Given the Kafka producer service is available
    When I publish a message "test-message" to the topic
    Then the message should be sent successfully

  Scenario: Handle publishing failure
    Given the Kafka producer service throws an error
    When I publish a message "fail-message" via the REST endpoint
    Then the response status should be 500

  Scenario: Successfully publish via REST endpoint
    Given the Kafka producer service is available
    When I publish a message "rest-message" via the REST endpoint
    Then the response status should be 200
    And the response body should be "Messages published successfully."

@bbgmiddleware
Feature: verify end to end automation testing of bbg middleware

  @oxid_to_sap_stage @scenario1_stage
  Scenario: Oxid to SAP e2e testing (Staging)
    Given I perform the end-to-end testing of oxid to SAP
    Then I verify no issue occurred in all oxid to SAP end to end processes

  @oxid_to_sage_stage @scenario2_stage
  Scenario: Oxid to Sage e2e testing (Staging)
    Given I perform the end-to-end testing of oxid to Sage
    Then I verify no issue occurred in all oxid to sage end to end processes

  @pm_to_sap_stage @scenario3_stage
  Scenario: Plenty Market to SAP e2e testing (Staging)
    Given I perform the end-to-end testing of Plenty Market to SAP
    Then I verify no issue occurred in all plenty to Sap end to end processes

  @pm_to_sage_stage @scenario4_stage
  Scenario: Plenty Market to Sage e2e testing (Staging)
    Given I perform the end-to-end testing of Plenty Market to Sage
    Then I verify no issue occurred in all plenty to Sage end to end processes

#  @commercetools_to_sap @scenario5_stage
#  Scenario: Commerce tools end to end test
#   Given I performed end to end testing commerce tools to Sap

  @skorders_to_sage_stage @scenario6_stage
  Scenario: Sk orders to Sage e2e testing (Staging)
    Given I perform the end-to-end testing of skOrders to Sage
    Then I verify no issue occurred in all skOrders to Sage end to end processes

 @wilson_return_order_to_sap @scenario7_stage
 Scenario: Wilson to SAP return order (Staging)
   Given I use the api call to send the return order to SAP
   Then I should see the cifNo in new relic for return order

  @wilson_inspection_order_to_sap @scenario8_stage
  Scenario: Wilson to SAP return inspection (Staging)
    Given I use the api call to send the return inspection to SAP
    Then I should see the cifNo in new relic for return inspection
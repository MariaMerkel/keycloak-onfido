# Unofficial Onfido Keycloak SPI
This Keycloak provider allows you to authenticate Keycloak users via Onfido's facial similarity checks.

## Prerequisites
To use this SPI, you will need to have a direct contract with Onfido covering at least one facial similarity report type.

Users must already have a completed document report in Onfido.

## Configuration
Upon installation of the SPI, create an authentication flow that contains one required Generic sub-flow, which contains the following steps:

- A "Username Form" step
- A "Onfido Facial Similarity" step, in whose config you must set the Onfido region, API token and the report type to use

### Report Type Considerations
The "photo" and "video" report types can fall back to manual review by Onfido, which will take longer than the HTTP timeout and would then cause authentications to fail. Instead of "photo", consider using "photo_fully_auto" if your Onfido contract allows. The "video" report type can also take relatively long to process even if it is processed fully automatically, which can (but usually won't) exceed the HTTP timeout.

Different report types have different security considerations. Generally, "video" is more secure than "motion" and "motion" is more secure than "video". Onfido does not officially support any report types for the purpose of authenticating users (these products are only officially supported for identity verification in a customer due diligence context), so whether this SPI is suitable for your use case depends on your risk appetite.

## User Mapping
This SPI uses a custom user attribute, "onfido_applicant_id", to store the Onfido applicant ID.
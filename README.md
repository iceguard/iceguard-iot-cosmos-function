[![Build Status](https://dev.azure.com/ice-guard-surveillance-system/igss/_apis/build/status/iceguard.iceguard-iot-cosmos-function?branchName=master)](https://dev.azure.com/ice-guard-surveillance-system/igss/_build/latest?definitionId=17&branchName=master)

# Build & deploy

`
mvn clean install 
`

`
mvn azure-functions:deploy
`

# Run locally

You need to have a local.settings.json with all needed values (see template bellow)
in order to run the function locally.

`
mvn azure-functions:run
`

# Connect to logs

`
az webapp log tail --name <functio name> --resource-group <resourcegroup>
`

# Create connection

`
func azure functionapp fetch-app-settings <function-name>
`

requires dotnet (core)

# Template for local.settings.json

```
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "FUNCTIONS_EXTENSION_VERSION": "~1",
    "AzureWebJobsDashboard": "",
    "WEBSITE_RUN_FROM_PACKAGE": "1",
    "WEBSITE_CONTENTSHARE": "",
    "WEBSITE_CONTENTAZUREFILECONNECTIONSTRING": "",
    "igss_COSMOS_DB": "",
    "igss-iothub_events_IOTHUB": "",
    "storageAccount": "",
    "collection-name-COSMOS": ""
  },
  "ConnectionStrings": {}
}
```
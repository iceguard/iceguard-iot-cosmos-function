# Build & deploy

`
mvn clean install 
`

`
mvn azure-functions:deploy
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
    "storageAccount": ""
  },
  "ConnectionStrings": {}
}
```
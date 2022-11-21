package Pages;

import base.Base;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.models.CloudEvent;
import com.azure.core.models.CloudEventDataFormat;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.flowable.eventsubscription.api.EventSubscription;
import org.openqa.selenium.WebDriver;

public class CommercetoolsToSapPage extends Base {

    public CommercetoolsToSapPage(WebDriver driver) {
        super(driver);
    }

    EventGridPublisherClient<CloudEvent> cloudEventPublisherClient;

    public void eventGridClientCreate()
    {
// Create a client to send events of CloudEvent schema (com.azure.core.models.CloudEvent)
                cloudEventPublisherClient = new EventGridPublisherClientBuilder()
                .endpoint("AZURE_EVENTGRID_CLOUDEVENT_ENDPOINT") // make sure it accepts CloudEvent
                .credential(new AzureKeyCredential("AZURE_EVENTGRID_CLOUDEVENT_KEY"))
                .buildCloudEventPublisherClient();
    }

    public void sendCloudEvent()
    {

        CloudEvent cloudEventDataObject = new CloudEvent("/cloudevents/example/source", "Example.EventType",
                BinaryData.fromObject("Haris"), CloudEventDataFormat.JSON, "application/json");

        // Send a single CloudEvent
        cloudEventPublisherClient.sendEvent(cloudEventDataObject);
    }

    public void executEventGrid()
    {
        eventGridClientCreate();
        sendCloudEvent();
    }
}

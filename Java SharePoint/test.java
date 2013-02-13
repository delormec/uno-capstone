import org.joda.time.LocalDateTime; 
import org.odata4j.consumer.ODataConsumer; 
import org.odata4j.consumer.ODataConsumers; 
import org.odata4j.consumer.behaviors.OClientBehaviors; 
import org.odata4j.core.OEntity; 
import org.odata4j.core.OProperties; 

public class test 
{ 
        private static void odata4jtest() 
        { 
                String SERVICE_ROOT_URI = "http://97.74.198.46/test/_vti_bin/listdata.svc/"; 
                
                
                //ODataConsumer c = ODataConsumers.newBuilder(SERVICE_ROOT_URI).setClientBehaviors(OClientBehaviors.basicAuth("delormec", "hSavF21!")).build(); 
                ODataConsumer c = ODataConsumers.newBuilder(SERVICE_ROOT_URI).setClientBehaviors(OClientBehaviors.basicAuth("testuser", "asdasd123!")).build(); 
        
                
                for (OEntity entity: c.getEntities("Contacts").execute()) 
                { 
                        System.out.println("*************"); 
                        System.out.println(entity.getProperty("FirstName").getValue().toString()); 
                        System.out.println(entity.getProperty("LastName").getValue().toString()); 
                        
                } 
                
                
            //Adding to an existing list 
            //OEntity newProduct = c.createEntity("Contacts") 
            //        .properties(OProperties.string("FirstName", "Heyo")) 
            //        .properties(OProperties.string("LastName", "Test2")) 
            //        .execute(); 

        } 
        
        public static void main(String[] argv) 
        { 
                
                
                odata4jtest(); 
        } 
}
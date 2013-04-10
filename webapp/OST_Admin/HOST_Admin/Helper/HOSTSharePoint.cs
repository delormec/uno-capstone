using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HOST_Admin.Models.ViewModel;
using System.Data.Objects.DataClasses;
using System.Reflection;
using Microsoft.Data.OData;
using Microsoft.Data.Edm.Validation;
using Microsoft.Data.Edm.Csdl;
using Microsoft.Data.Edm;
using System.Xml;
using System.Net;
using Microsoft.Data.Edm.Annotations;
using System.IO;
using System.Xml.Linq;

namespace HOST_Admin.Helper
{
    /// <summary>
    /// Class that interacts with SharePoint SOAP protocol.
    /// </summary>
    public class HOSTSharePoint
    {
        //public static List<SharePointSOAPResponseViewModel> generateForm()
        //{
        //    SharePointSOAPRequestViewModel spsrvm = new SharePointSOAPRequestViewModel();
        //    spsrvm.url = "http://habitat.taic.net/omaha/unotestsite/_vti_bin/lists.asmx";
        //    spsrvm.user_name = "CDelorme";
        //    spsrvm.password = "CDelorme463";
        //    spsrvm.domain = "xtranet";
        //    spsrvm.list_name = "Construction Atlas Test";


        //    return generateForm(spsrvm);
        //}

        /// <summary>
        /// 
        /// </summary>
        /// <param name="spsrvm"></param>
        /// <returns></returns>
        public static List<SharePointSOAPResponseViewModel> generateForm(SharePointSOAPRequestViewModel spsrvm)
        {
            WebClient client = new WebClient();

            List<SharePointSOAPResponseViewModel>  responseViewModelList = new List<SharePointSOAPResponseViewModel>();


            //Set up windows authentication, Uri(url) doesn't really seem to matter
            //I've seen it set as just the base domain of a url example: http://google.com
            CredentialCache cc = new CredentialCache();
            cc.Add(new Uri(spsrvm.url), "NTLM", new NetworkCredential(spsrvm.user_name, spsrvm.password, spsrvm.domain));
            client.Credentials = cc;

            //required headers
            client.Headers.Add("SOAPAction", "http://schemas.microsoft.com/sharepoint/soap/GetList");
            client.Headers.Add("Content-Type", "text/xml; charset=utf-8");


            //construct the SOAP envelope and place list name inside of it
            var xmlEnvelope = "";
            xmlEnvelope += "<?xml version='1.0' encoding='utf-8' ?>";
            xmlEnvelope += "<soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>";
            xmlEnvelope += "<soap:Body>";
            xmlEnvelope += "<GetList xmlns='http://schemas.microsoft.com/sharepoint/soap/'>";
            xmlEnvelope += "<listName>";
            xmlEnvelope += spsrvm.list_name;
            xmlEnvelope += "</listName>";
            xmlEnvelope += "</GetList>";
            xmlEnvelope += "</soap:Body>";
            xmlEnvelope += "</soap:Envelope>";

            try
            {
                //Send SOAP packet
                var result = client.UploadString(spsrvm.url, xmlEnvelope);

                //Parse the result
                XDocument xdoc = XDocument.Parse(result);

                //need this for soap:Envelope and soap:Body
                //XNamespace xmlns = "http://schemas.xmlsoap.org/soap/envelope/";

                //Required
                XNamespace xmlns = "http://schemas.microsoft.com/sharepoint/soap/";

                foreach (XElement element in xdoc.Descendants(xmlns + "Field"))
                {
                    //Don't want hidden or read only values, and it must have a colname specified. 
                    //This takes advantage of a short circuit evaluation as many elements don't have the attribute specified.
                    //Even hiding all fields that don't meet these criteria we are still left with some fields we don't want the user to mess with.
                    //There may be a better way to do limit the search.
                    if (element.Attribute("Hidden") == null || element.Attribute("Hidden").Value == "False")
                        if (element.Attribute("ReadOnly") == null || element.Attribute("ReadOnly").Value == "False")
                            if (element.Attribute("ColName") != null)
                            {
                                SharePointSOAPResponseViewModel responseViewModel = new SharePointSOAPResponseViewModel(element.Attribute("DisplayName").Value, element.Attribute("Type").Value);

                                //If its a choice question display all my choices.
                                if (element.Attribute("Type").Value == "Choice" || element.Attribute("Type").Value == "MultiChoice")
                                {
                                    List<String> choices = new List<string>();

                                    foreach (XElement choice in element.Descendants(xmlns + "CHOICE"))
                                        choices.Add(choice.Value);

                                    //add list of choices to my field element
                                    responseViewModel.choices = choices;
                                }

                                //add the element to list of fields
                                responseViewModelList.Add(responseViewModel);
                            }
                }//end foreach
            }//end try
            catch (WebException e)
            {
                //remote server threw an error
                if (e.Status == WebExceptionStatus.ProtocolError)
                {
                    Console.WriteLine("Status Code : {0}", ((HttpWebResponse)e.Response).StatusCode);
                    Console.WriteLine("Status Description : {0}", ((HttpWebResponse)e.Response).StatusDescription);

                    System.IO.Stream stream = e.Response.GetResponseStream();

                    StreamReader sre = new StreamReader(stream);

                    string s = sre.ReadToEnd();
                    Console.Write(s);

                }

                return null;
            }

            return responseViewModelList;
        }



        //Uses EDM/Atom, not going to use this.
        public static String test1()
        {

            IEdmModel model = GetModel();

            /*
            HTTPClientRequestMessage message = new HTTPClientRequestMessage("http://habitat.taic.net/omaha/unotestsite/_vti_bin/listdata.svc/");
            //message.SetHeader("Accept", "application/json");
            message.SetHeader("Accept", "application/json");
            message.SetHeader("DataServiceVersion", ODataUtils.ODataVersionToString(ODataVersion.V2));
            message.SetHeader("MaxDataServiceVersion", ODataUtils.ODataVersionToString(ODataVersion.V2));
            */

            /*
            var webRequest = (HttpWebRequest)HttpWebRequest.Create("http://habitat.taic.net/omaha/unotestsite/_vti_bin/listdata.svc/");
            CredentialCache cc = new CredentialCache();
            cc.Add(new Uri("http://habitat.taic.net"), "NTLM", new NetworkCredential("CDelorme", "CDelorme463", "xtranet"));
            webRequest.Credentials = cc;
            webRequest.Method = "GET";
            var response = webRequest.GetResponse();
            */

            HTTPClientRequestMessage message = new HTTPClientRequestMessage("http://habitat.taic.net/omaha/unotestsite/_vti_bin/listdata.svc/");
            //message.SetHeader("Accept", "application/json");

            using (ODataMessageReader messageReader = new ODataMessageReader(message.GetResponse(), new ODataMessageReaderSettings(), model))
            {

                String str_test = "";

                str_test += model.SchemaElementsAcrossModels().OfType<IEdmEntityType>().Count().ToString();

                IEdmEntityType testy = (IEdmEntityType)model.FindType("Microsoft.SharePoint.DataService.ConstructionAtlasTestACQUISITION_TYPEValue");

                IEdmProperty value = testy.FindProperty("Value");


                //str_test += model.VocabularyAnnotations.Count();


                //we have no annotations
                foreach (IEdmVocabularyAnnotation annotation in value.VocabularyAnnotations(model))
                {
                    str_test += "in here";
                    if (annotation.Term.TermKind == EdmTermKind.Type)
                    {
                        IEdmTypeAnnotation typeAnnotation = (IEdmTypeAnnotation)annotation;
                        str_test += "\nName: " + typeAnnotation.Term.Name + "\n";
                    }
                }


                //str_test += testy.Properties().First().


                foreach (IEdmEntityContainer container in model.EntityContainers())
                {
                    //str_test += container.EntitySets().Count().ToString();

                    foreach (IEdmEntitySet set in container.EntitySets())
                    {
                        if (set.Name == "ConstructionAtlasTestACQUISITION_TYPE")
                        {
                            //set.GetType().GetFields().ToList().ForEach(p => str_test += p.Name);

                            str_test += set.Name + "\n";
                            set.ElementType.DeclaredProperties.ToList().ForEach(p => str_test += "\t" + p.Name + "\n");
                            set.ElementType.DeclaredKey.ToList().ForEach(p => str_test += "\t" + p.Name + "\n");
                            str_test += set.ElementType.ToTraceString();
                            str_test += model.FindEntityContainer("UNOTestSiteDataContext").FindEntitySet("ConstructionAtlasTestACQUISITION_TYPE").ElementType.Namespace;

                        }

                    }
                }

                foreach (IEdmEntityType type in model.SchemaElementsAcrossModels().OfType<IEdmEntityType>())
                {
                    str_test += "\n" + type.FullName() + "\n";

                    //type.NavigationProperties().ToList().ForEach(p => str_test += p.Name);

                    foreach (IEdmNavigationProperty property in type.NavigationProperties())
                    {
                        if (property.Name == "ACQUISITION_TYPE")
                            str_test += property.Name + "-" + property.Name + "\n";

                    }

                    foreach (IEdmProperty property in type.Properties())
                    {
                        //str_test += "\t" + property.Name + " - " + property.Type.AsEntity().p + " - " + property.PropertyKind.ToString() + "\n";
                        if (property.Type.IsEntity())
                        {
                            str_test += "\t" + property.Name + " - " + property.Type.TypeKind().ToString() + " - " + property.Type.AsEntity().StructuralProperties().First().Name + " - " + property.PropertyKind.ToString() + "\n";
                        }
                        else
                        {
                            str_test += "\t" + property.Name + " - " + property.Type.TypeKind().ToString() + " - " + property.PropertyKind.ToString() + "\n";

                        }
                    }

                }

                //model.

                foreach (IEdmEntitySet type in model.SchemaElementsAcrossModels().OfType<IEdmEnumType>())
                {

                    str_test += type.Name;
                }
                return str_test;
                //return model.SchemaElementsAcrossModels().OfType<IEdmEntityType>().First().Properties().First().Type.ToString();


                //ODataReader reader = messageReader.CreateODataEntryReader();
                //ODataReader reader = messageReader.CreateODataFeedReader();
                //ODataReader reader = messageReader.CreateODataCollectionReader();
                //ODataCollectionReader reader = messageReader.CreateODataCollectionReader();
                ODataReader reader = messageReader.CreateODataEntryReader();
                //ODataE



                while (reader.Read())
                {
                    switch (reader.State)
                    {
                        case ODataReaderState.EntryEnd:
                            {
                                ODataEntry entry = (ODataEntry)reader.Item;
                                return entry.Id.First().ToString();
                            }

                    }

                }
                /*
                while (reader.Read())
                {
                    switch (reader.State)
                    {
                        case ODataReaderState.EntryEnd :
                            {
                                ODataEntry entry = (ODataEntry)reader.Item;
                                return entry.ReadLink.AbsoluteUri;
                            }
                    }

                }
                */
            }

            return "no";
        }


        public static IEdmModel GetModel()
        {
            var webRequest = (HttpWebRequest)HttpWebRequest.Create("http://habitat.taic.net/omaha/unotestsite/_vti_bin/listdata.svc/$metadata");

            //webRequest = (HttpWebRequest)WebRequest.Create(new Uri(uri));
            CredentialCache cc = new CredentialCache();
            cc.Add(new Uri("http://habitat.taic.net"), "NTLM", new NetworkCredential("CDelorme", "CDelorme463", "xtranet"));
            webRequest.Credentials = cc;


            webRequest.Method = "GET";
            var response = webRequest.GetResponse();
            using (XmlReader xmlReader = XmlReader.Create(response.GetResponseStream()))
            {
                IEdmModel model;
                IEnumerable<EdmError> errors;
                if (EdmxReader.TryParse(xmlReader, out model, out errors))
                {
                    return model;
                }

                throw new Exception("Error parsing edm model");
            }
        }
    }

}
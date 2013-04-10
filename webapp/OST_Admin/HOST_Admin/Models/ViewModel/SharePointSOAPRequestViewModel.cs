using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace HOST_Admin.Models.ViewModel
{
    /// <summary>
    /// Stores information used to call SharePoint's SOAP interface for GetList.
    /// </summary>
    public class SharePointSOAPRequestViewModel
    {
        public String user_name { get; set; }
        public String password { get; set; }
        public String domain { get; set; }
        public String url { get; set; }
        public String list_name { get; set; }
    }
}

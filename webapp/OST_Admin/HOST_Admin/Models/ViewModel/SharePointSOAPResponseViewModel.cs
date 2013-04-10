using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace HOST_Admin.Models.ViewModel
{
    /// <summary>
    /// Stores information retrieved from SharePointSOAP call. 
    /// Contains a list's fields/types, and if the type is Choice/MultiChoice then it contains all the choices.
    /// </summary>
    public class SharePointSOAPResponseViewModel
    {
        public String field_name { get; set; }
        public String field_type { get; set; }
        public List<String> choices { get; set; }

        public SharePointSOAPResponseViewModel(String field_name, String field_type)
        {
            this.field_name = field_name;
            this.field_type = field_type;
        }
    }

}

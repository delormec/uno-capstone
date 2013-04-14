using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace HOST_Admin
{
    /// <summary>
    /// Set up routing for API controllers in MVC
    /// </summary>
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );
        }
    }
}

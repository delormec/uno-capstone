using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Routing;

namespace HOST_Admin
{
    /// <summary>
    /// Set up url routing in MVC
    /// </summary>
    public class RouteConfig
    {
        /// <summary>
        /// Set up url -> function mapping within MVC
        /// </summary>
        /// <param name="routes"></param>
        public static void RegisterRoutes(RouteCollection routes)
        {
            routes.IgnoreRoute("{resource}.axd/{*pathInfo}");

            routes.MapRoute(
                name: "Default",
                url: "{controller}/{action}/{id}",
                defaults: new { controller = "Account", action = "LogIn", id = UrlParameter.Optional }
            );
        }
    }
}
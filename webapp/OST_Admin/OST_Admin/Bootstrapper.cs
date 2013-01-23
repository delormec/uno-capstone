using System.Web.Http;
using Microsoft.Practices.Unity;
using OST_Admin.Models.Repository;
using OST_Admin.Models;

using System.Data.EntityClient;
using System.Web.Mvc;
using OST_Admin.Helper;

namespace OST_Admin
{
    public static class Bootstrapper
    {
        public static void Initialise()
        {
            var container = BuildUnityContainer();
            DependencyResolver.SetResolver(new Unity.Mvc3.UnityDependencyResolver(container));
            GlobalConfiguration.Configuration.DependencyResolver = new Unity.WebApi.UnityDependencyResolver(container);

            //used to pass list of non-base types into a List<Question>
            ModelBinders.Binders.Add(typeof(Question), new QuestionModelBinder());
        }

        private static IUnityContainer BuildUnityContainer()
        {
            var container = new UnityContainer();

            // register all your components with the container here
            // e.g. container.RegisterType<ITestService, TestService>();            

            container.RegisterType<IFormRepository, FormRepository>();
            container.RegisterType<IOSTDataContext, OSTDataContext>(new InjectionConstructor());

            return container;
        }
    }
}
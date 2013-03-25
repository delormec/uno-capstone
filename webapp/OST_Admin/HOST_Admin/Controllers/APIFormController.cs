using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using HOST_Admin.Models.Repository;
using HOST_Admin.Models;

namespace HOST_Admin.Controllers
{
    /// <summary>
    /// Controller that acts as an external system interface with the Android app.
    /// </summary>
    public class APIFormController : ApiController
    {
        /// <summary>
        /// Reference to form repository.
        /// </summary>
        private readonly IFormRepository _formRepository;

        public APIFormController(IFormRepository formRepository)
        {
            _formRepository = formRepository;
        }

        /// <summary>
        /// GET: Displays XML that contains all 'active' forms in the database. Acts as an ODATA endpoint.
        /// </summary>
        /// <returns></returns>
        [Queryable]
        public IQueryable<String> Get()
        {
            IQueryable<Form> forms = _formRepository.GetAll().Where(p => p.Active == true);
            List<String> form_ids = new List<string>();

            forms.ToList().ForEach(p => form_ids.Add(p.FormId.ToString()));

            return form_ids.AsQueryable();
        }

        /// <summary>
        /// GET/id: Returns XML representation of a form. Used in the android app.
        /// </summary>
        /// <param name="id">id of a form as provided by GET</param>
        /// <returns>XML representation of form</returns>
        public String Get(int id)
        {
            String xml = XML.fromForm(_formRepository.getFormById(id));

            return xml;
        }
    }
}

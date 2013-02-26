using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using OST_Admin.Models.Repository;
using OST_Admin.Models;

namespace OST_Admin.Controllers
{
    public class APIFormController : ApiController
    {
        private readonly IFormRepository _formRepository;

        public APIFormController(IFormRepository formRepository)
        {
            _formRepository = formRepository;
        }

        // GET api/apiform
        public IQueryable<String> Get()
        {
            IQueryable<Form> forms = _formRepository.GetAll().Where(p => p.Active == true);
            List<String> form_ids = new List<string>();

            forms.ToList().ForEach(p => form_ids.Add(p.FormId.ToString()));

            return form_ids.AsQueryable();
        }

        // GET api/apiform/5
        public String Get(int id)
        {
            String xml = XML.fromForm(_formRepository.getFormById(id));

            return xml;
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using OST_Admin.Models.Repository;

namespace OST_Admin.Controllers
{
    public class ValuesController : ApiController
    {
        private readonly IFormRepository formRepository;

        public ValuesController()
        {
            formRepository = new FormRepository();
        }

        // GET api/values
        [Queryable]
        public IQueryable<Form> Get()
        {
            return formRepository.GetAll();
        }

        // GET api/values/5
        public string Get(int id)
        {
            return "value";
        }

        // POST api/values
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        public void Delete(int id)
        {
        }
    }
}
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
    public class ValuesController : ApiController
    {
        private readonly IFormRepository _formRepository;

        public ValuesController(IFormRepository formRepository)
        {
            _formRepository = formRepository;
        }

        // GET api/values
        [Queryable]
        public IQueryable<Form> Get()
        {
            return _formRepository.GetAll();
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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Data.OData;
using System.Web.Http;

namespace OST_Admin.Models.Repository
{
    public class FormRepository : IFormRepository
    {
        OSTDataContext _databaseContext;

        public FormRepository(IOSTDataContext databaseContext)
        {
            _databaseContext = new OSTDataContext();
        }

       
        public System.Linq.IQueryable<Form> GetAll()
        {
            return _databaseContext.Forms;
        }


        public Form getForm(int id)
        {
            return _databaseContext.Forms.Where(p => p.FormId == id).Single();
        }
    }
}
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace OST_Admin.Models.Repository
{
    public interface IFormRepository
    {

        IQueryable<Form> GetAll();
        Form getFormById(int id);
        void updateForm(Form form);
        void deleteFormById(int id);
        void testy();

    }
}
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Capstone.Models.Repository
{
    public interface IFormRepository
    {

        IQueryable<Form> GetAll();
    }
}
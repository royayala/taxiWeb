package com.sinapsistech.taxiWeb.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.sinapsistech.taxiWeb.model.VehiculoPersona;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Persona;
import com.sinapsistech.taxiWeb.model.Vehiculo;

/**
 * Backing bean for VehiculoPersona entities.
 * <p/>
 * This class provides CRUD functionality for all VehiculoPersona entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class VehiculoPersonaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving VehiculoPersona entities
    */

   private Integer id;

   public Integer getId()
   {
      return this.id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   private VehiculoPersona vehiculoPersona;

   public VehiculoPersona getVehiculoPersona()
   {
      return this.vehiculoPersona;
   }

   public void setVehiculoPersona(VehiculoPersona vehiculoPersona)
   {
      this.vehiculoPersona = vehiculoPersona;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(unitName = "taxiWeb-persistence-unit", type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      this.conversation.setTimeout(1800000L);
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
         this.conversation.setTimeout(1800000L);
      }

      if (this.id == null)
      {
         this.vehiculoPersona = this.example;
      }
      else
      {
         this.vehiculoPersona = findById(getId());
      }
   }

   public VehiculoPersona findById(Integer id)
   {

      return this.entityManager.find(VehiculoPersona.class, id);
   }

   /*
    * Support updating and deleting VehiculoPersona entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.vehiculoPersona);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.vehiculoPersona);
            return "view?faces-redirect=true&id=" + this.vehiculoPersona.getIdVehiculoPersona();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         VehiculoPersona deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getVehiculoPersonas().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Persona personaByIdChofer = deletableEntity.getPersonaByIdChofer();
         personaByIdChofer.getVehiculoPersonasForIdChofer().remove(deletableEntity);
         deletableEntity.setPersonaByIdChofer(null);
         this.entityManager.merge(personaByIdChofer);
         Persona personaByIdPersona = deletableEntity.getPersonaByIdPersona();
         personaByIdPersona.getVehiculoPersonasForIdPersona().remove(deletableEntity);
         deletableEntity.setPersonaByIdPersona(null);
         this.entityManager.merge(personaByIdPersona);
         Vehiculo vehiculo = deletableEntity.getVehiculo();
         vehiculo.getVehiculoPersonas().remove(deletableEntity);
         deletableEntity.setVehiculo(null);
         this.entityManager.merge(vehiculo);
         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching VehiculoPersona entities with pagination
    */

   private int page;
   private long count;
   private List<VehiculoPersona> pageItems;

   private VehiculoPersona example = new VehiculoPersona();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public VehiculoPersona getExample()
   {
      return this.example;
   }

   public void setExample(VehiculoPersona example)
   {
      this.example = example;
   }

   public String search()
   {
      this.page = 0;
      return null;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<VehiculoPersona> root = countCriteria.from(VehiculoPersona.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<VehiculoPersona> criteria = builder.createQuery(VehiculoPersona.class);
      root = criteria.from(VehiculoPersona.class);
      TypedQuery<VehiculoPersona> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<VehiculoPersona> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idVehiculoPersona = this.example.getIdVehiculoPersona();
      if (idVehiculoPersona != 0)
      {
         predicatesList.add(builder.equal(root.get("idVehiculoPersona"), idVehiculoPersona));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      Persona personaByIdChofer = this.example.getPersonaByIdChofer();
      if (personaByIdChofer != null)
      {
         predicatesList.add(builder.equal(root.get("personaByIdChofer"), personaByIdChofer));
      }
      Persona personaByIdPersona = this.example.getPersonaByIdPersona();
      if (personaByIdPersona != null)
      {
         predicatesList.add(builder.equal(root.get("personaByIdPersona"), personaByIdPersona));
      }
      Vehiculo vehiculo = this.example.getVehiculo();
      if (vehiculo != null)
      {
         predicatesList.add(builder.equal(root.get("vehiculo"), vehiculo));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<VehiculoPersona> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back VehiculoPersona entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<VehiculoPersona> getAll()
   {

      CriteriaQuery<VehiculoPersona> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(VehiculoPersona.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(VehiculoPersona.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final VehiculoPersonaBean ejbProxy = this.sessionContext.getBusinessObject(VehiculoPersonaBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Integer.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((VehiculoPersona) value).getIdVehiculoPersona());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private VehiculoPersona add = new VehiculoPersona();

   public VehiculoPersona getAdd()
   {
      return this.add;
   }

   public VehiculoPersona getAdded()
   {
      VehiculoPersona added = this.add;
      this.add = new VehiculoPersona();
      return added;
   }
}

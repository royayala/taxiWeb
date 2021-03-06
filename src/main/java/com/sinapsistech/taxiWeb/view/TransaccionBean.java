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

import com.sinapsistech.taxiWeb.model.Transaccion;
import com.sinapsistech.taxiWeb.model.Carrera;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Persona;
import com.sinapsistech.taxiWeb.model.PersonaServicio;
import com.sinapsistech.taxiWeb.model.TipoTransaccion;
import com.sinapsistech.taxiWeb.model.Vehiculo;
import com.sinapsistech.taxiWeb.model.VehiculoAfiliacion;

/**
 * Backing bean for Transaccion entities.
 * <p/>
 * This class provides CRUD functionality for all Transaccion entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TransaccionBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Transaccion entities
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

   private Transaccion transaccion;

   public Transaccion getTransaccion()
   {
      return this.transaccion;
   }

   public void setTransaccion(Transaccion transaccion)
   {
      this.transaccion = transaccion;
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
         this.transaccion = this.example;
      }
      else
      {
         this.transaccion = findById(getId());
      }
   }

   public Transaccion findById(Integer id)
   {

      return this.entityManager.find(Transaccion.class, id);
   }

   /*
    * Support updating and deleting Transaccion entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.transaccion);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.transaccion);
            return "view?faces-redirect=true&id=" + this.transaccion.getIdTransaccion();
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
         Transaccion deletableEntity = findById(getId());
         Carrera carrera = deletableEntity.getCarrera();
         carrera.getTransaccions().remove(deletableEntity);
         deletableEntity.setCarrera(null);
         this.entityManager.merge(carrera);
         Compania compania = deletableEntity.getCompania();
         compania.getTransaccions().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Persona persona = deletableEntity.getPersona();
         persona.getTransaccions().remove(deletableEntity);
         deletableEntity.setPersona(null);
         this.entityManager.merge(persona);
         PersonaServicio personaServicio = deletableEntity.getPersonaServicio();
         personaServicio.getTransaccions().remove(deletableEntity);
         deletableEntity.setPersonaServicio(null);
         this.entityManager.merge(personaServicio);
         TipoTransaccion tipoTransaccion = deletableEntity.getTipoTransaccion();
         tipoTransaccion.getTransaccions().remove(deletableEntity);
         deletableEntity.setTipoTransaccion(null);
         this.entityManager.merge(tipoTransaccion);
         Vehiculo vehiculo = deletableEntity.getVehiculo();
         vehiculo.getTransaccions().remove(deletableEntity);
         deletableEntity.setVehiculo(null);
         this.entityManager.merge(vehiculo);
         VehiculoAfiliacion vehiculoAfiliacion = deletableEntity.getVehiculoAfiliacion();
         vehiculoAfiliacion.getTransaccions().remove(deletableEntity);
         deletableEntity.setVehiculoAfiliacion(null);
         this.entityManager.merge(vehiculoAfiliacion);
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
    * Support searching Transaccion entities with pagination
    */

   private int page;
   private long count;
   private List<Transaccion> pageItems;

   private Transaccion example = new Transaccion();

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

   public Transaccion getExample()
   {
      return this.example;
   }

   public void setExample(Transaccion example)
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
      Root<Transaccion> root = countCriteria.from(Transaccion.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Transaccion> criteria = builder.createQuery(Transaccion.class);
      root = criteria.from(Transaccion.class);
      TypedQuery<Transaccion> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Transaccion> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idTransaccion = this.example.getIdTransaccion();
      if (idTransaccion != 0)
      {
         predicatesList.add(builder.equal(root.get("idTransaccion"), idTransaccion));
      }
      Carrera carrera = this.example.getCarrera();
      if (carrera != null)
      {
         predicatesList.add(builder.equal(root.get("carrera"), carrera));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      Persona persona = this.example.getPersona();
      if (persona != null)
      {
         predicatesList.add(builder.equal(root.get("persona"), persona));
      }
      PersonaServicio personaServicio = this.example.getPersonaServicio();
      if (personaServicio != null)
      {
         predicatesList.add(builder.equal(root.get("personaServicio"), personaServicio));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Transaccion> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Transaccion entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Transaccion> getAll()
   {

      CriteriaQuery<Transaccion> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Transaccion.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Transaccion.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TransaccionBean ejbProxy = this.sessionContext.getBusinessObject(TransaccionBean.class);

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

            return String.valueOf(((Transaccion) value).getIdTransaccion());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Transaccion add = new Transaccion();

   public Transaccion getAdd()
   {
      return this.add;
   }

   public Transaccion getAdded()
   {
      Transaccion added = this.add;
      this.add = new Transaccion();
      return added;
   }
}

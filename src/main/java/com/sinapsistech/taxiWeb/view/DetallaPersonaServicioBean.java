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

import com.sinapsistech.taxiWeb.model.DetallaPersonaServicio;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.PersonaServicio;
import com.sinapsistech.taxiWeb.model.Vehiculo;

/**
 * Backing bean for DetallaPersonaServicio entities.
 * <p/>
 * This class provides CRUD functionality for all DetallaPersonaServicio entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class DetallaPersonaServicioBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving DetallaPersonaServicio entities
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

   private DetallaPersonaServicio detallaPersonaServicio;

   public DetallaPersonaServicio getDetallaPersonaServicio()
   {
      return this.detallaPersonaServicio;
   }

   public void setDetallaPersonaServicio(DetallaPersonaServicio detallaPersonaServicio)
   {
      this.detallaPersonaServicio = detallaPersonaServicio;
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
         this.detallaPersonaServicio = this.example;
      }
      else
      {
         this.detallaPersonaServicio = findById(getId());
      }
   }

   public DetallaPersonaServicio findById(Integer id)
   {

      return this.entityManager.find(DetallaPersonaServicio.class, id);
   }

   /*
    * Support updating and deleting DetallaPersonaServicio entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.detallaPersonaServicio);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.detallaPersonaServicio);
            return "view?faces-redirect=true&id=" + this.detallaPersonaServicio.getIdDetPersonaServicio();
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
         DetallaPersonaServicio deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getDetallaPersonaServicios().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         PersonaServicio personaServicio = deletableEntity.getPersonaServicio();
         personaServicio.getDetallaPersonaServicios().remove(deletableEntity);
         deletableEntity.setPersonaServicio(null);
         this.entityManager.merge(personaServicio);
         Vehiculo vehiculo = deletableEntity.getVehiculo();
         vehiculo.getDetallaPersonaServicios().remove(deletableEntity);
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
    * Support searching DetallaPersonaServicio entities with pagination
    */

   private int page;
   private long count;
   private List<DetallaPersonaServicio> pageItems;

   private DetallaPersonaServicio example = new DetallaPersonaServicio();

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

   public DetallaPersonaServicio getExample()
   {
      return this.example;
   }

   public void setExample(DetallaPersonaServicio example)
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
      Root<DetallaPersonaServicio> root = countCriteria.from(DetallaPersonaServicio.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<DetallaPersonaServicio> criteria = builder.createQuery(DetallaPersonaServicio.class);
      root = criteria.from(DetallaPersonaServicio.class);
      TypedQuery<DetallaPersonaServicio> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<DetallaPersonaServicio> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idDetPersonaServicio = this.example.getIdDetPersonaServicio();
      if (idDetPersonaServicio != 0)
      {
         predicatesList.add(builder.equal(root.get("idDetPersonaServicio"), idDetPersonaServicio));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      PersonaServicio personaServicio = this.example.getPersonaServicio();
      if (personaServicio != null)
      {
         predicatesList.add(builder.equal(root.get("personaServicio"), personaServicio));
      }
      Vehiculo vehiculo = this.example.getVehiculo();
      if (vehiculo != null)
      {
         predicatesList.add(builder.equal(root.get("vehiculo"), vehiculo));
      }
      String observaciones = this.example.getObservaciones();
      if (observaciones != null && !"".equals(observaciones))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("observaciones")), '%' + observaciones.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<DetallaPersonaServicio> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back DetallaPersonaServicio entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<DetallaPersonaServicio> getAll()
   {

      CriteriaQuery<DetallaPersonaServicio> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(DetallaPersonaServicio.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(DetallaPersonaServicio.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final DetallaPersonaServicioBean ejbProxy = this.sessionContext.getBusinessObject(DetallaPersonaServicioBean.class);

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

            return String.valueOf(((DetallaPersonaServicio) value).getIdDetPersonaServicio());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private DetallaPersonaServicio add = new DetallaPersonaServicio();

   public DetallaPersonaServicio getAdd()
   {
      return this.add;
   }

   public DetallaPersonaServicio getAdded()
   {
      DetallaPersonaServicio added = this.add;
      this.add = new DetallaPersonaServicio();
      return added;
   }
}

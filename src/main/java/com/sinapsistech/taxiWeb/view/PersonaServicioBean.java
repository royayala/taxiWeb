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

import com.sinapsistech.taxiWeb.model.PersonaServicio;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.DetallaPersonaServicio;
import com.sinapsistech.taxiWeb.model.Persona;
import com.sinapsistech.taxiWeb.model.Servicio;
import com.sinapsistech.taxiWeb.model.Tarifa;
import com.sinapsistech.taxiWeb.model.Transaccion;
import java.util.Iterator;

/**
 * Backing bean for PersonaServicio entities.
 * <p/>
 * This class provides CRUD functionality for all PersonaServicio entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class PersonaServicioBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving PersonaServicio entities
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

   private PersonaServicio personaServicio;

   public PersonaServicio getPersonaServicio()
   {
      return this.personaServicio;
   }

   public void setPersonaServicio(PersonaServicio personaServicio)
   {
      this.personaServicio = personaServicio;
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
         this.personaServicio = this.example;
      }
      else
      {
         this.personaServicio = findById(getId());
      }
   }

   public PersonaServicio findById(Integer id)
   {

      return this.entityManager.find(PersonaServicio.class, id);
   }

   /*
    * Support updating and deleting PersonaServicio entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.personaServicio);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.personaServicio);
            return "view?faces-redirect=true&id=" + this.personaServicio.getIdPersonaServicio();
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
         PersonaServicio deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getPersonaServicios().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Persona persona = deletableEntity.getPersona();
         persona.getPersonaServicios().remove(deletableEntity);
         deletableEntity.setPersona(null);
         this.entityManager.merge(persona);
         Servicio servicio = deletableEntity.getServicio();
         servicio.getPersonaServicios().remove(deletableEntity);
         deletableEntity.setServicio(null);
         this.entityManager.merge(servicio);
         Tarifa tarifa = deletableEntity.getTarifa();
         tarifa.getPersonaServicios().remove(deletableEntity);
         deletableEntity.setTarifa(null);
         this.entityManager.merge(tarifa);
         Iterator<DetallaPersonaServicio> iterDetallaPersonaServicios = deletableEntity.getDetallaPersonaServicios().iterator();
         for (; iterDetallaPersonaServicios.hasNext();)
         {
            DetallaPersonaServicio nextInDetallaPersonaServicios = iterDetallaPersonaServicios.next();
            nextInDetallaPersonaServicios.setPersonaServicio(null);
            iterDetallaPersonaServicios.remove();
            this.entityManager.merge(nextInDetallaPersonaServicios);
         }
         Iterator<Transaccion> iterTransaccions = deletableEntity.getTransaccions().iterator();
         for (; iterTransaccions.hasNext();)
         {
            Transaccion nextInTransaccions = iterTransaccions.next();
            nextInTransaccions.setPersonaServicio(null);
            iterTransaccions.remove();
            this.entityManager.merge(nextInTransaccions);
         }
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
    * Support searching PersonaServicio entities with pagination
    */

   private int page;
   private long count;
   private List<PersonaServicio> pageItems;

   private PersonaServicio example = new PersonaServicio();

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

   public PersonaServicio getExample()
   {
      return this.example;
   }

   public void setExample(PersonaServicio example)
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
      Root<PersonaServicio> root = countCriteria.from(PersonaServicio.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<PersonaServicio> criteria = builder.createQuery(PersonaServicio.class);
      root = criteria.from(PersonaServicio.class);
      TypedQuery<PersonaServicio> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<PersonaServicio> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idPersonaServicio = this.example.getIdPersonaServicio();
      if (idPersonaServicio != 0)
      {
         predicatesList.add(builder.equal(root.get("idPersonaServicio"), idPersonaServicio));
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
      Servicio servicio = this.example.getServicio();
      if (servicio != null)
      {
         predicatesList.add(builder.equal(root.get("servicio"), servicio));
      }
      Tarifa tarifa = this.example.getTarifa();
      if (tarifa != null)
      {
         predicatesList.add(builder.equal(root.get("tarifa"), tarifa));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<PersonaServicio> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back PersonaServicio entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<PersonaServicio> getAll()
   {

      CriteriaQuery<PersonaServicio> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(PersonaServicio.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(PersonaServicio.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final PersonaServicioBean ejbProxy = this.sessionContext.getBusinessObject(PersonaServicioBean.class);

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

            return String.valueOf(((PersonaServicio) value).getIdPersonaServicio());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private PersonaServicio add = new PersonaServicio();

   public PersonaServicio getAdd()
   {
      return this.add;
   }

   public PersonaServicio getAdded()
   {
      PersonaServicio added = this.add;
      this.add = new PersonaServicio();
      return added;
   }
}

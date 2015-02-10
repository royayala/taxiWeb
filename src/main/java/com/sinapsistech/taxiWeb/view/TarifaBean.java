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

import com.sinapsistech.taxiWeb.model.Tarifa;
import com.sinapsistech.taxiWeb.model.Carrera;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.PersonaServicio;
import java.util.Iterator;

/**
 * Backing bean for Tarifa entities.
 * <p/>
 * This class provides CRUD functionality for all Tarifa entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TarifaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Tarifa entities
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

   private Tarifa tarifa;

   public Tarifa getTarifa()
   {
      return this.tarifa;
   }

   public void setTarifa(Tarifa tarifa)
   {
      this.tarifa = tarifa;
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
         this.tarifa = this.example;
      }
      else
      {
         this.tarifa = findById(getId());
      }
   }

   public Tarifa findById(Integer id)
   {

      return this.entityManager.find(Tarifa.class, id);
   }

   /*
    * Support updating and deleting Tarifa entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.tarifa);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.tarifa);
            return "view?faces-redirect=true&id=" + this.tarifa.getIdTarifa();
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
         Tarifa deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getTarifas().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Iterator<PersonaServicio> iterPersonaServicios = deletableEntity.getPersonaServicios().iterator();
         for (; iterPersonaServicios.hasNext();)
         {
            PersonaServicio nextInPersonaServicios = iterPersonaServicios.next();
            nextInPersonaServicios.setTarifa(null);
            iterPersonaServicios.remove();
            this.entityManager.merge(nextInPersonaServicios);
         }
         Iterator<Carrera> iterCarreras = deletableEntity.getCarreras().iterator();
         for (; iterCarreras.hasNext();)
         {
            Carrera nextInCarreras = iterCarreras.next();
            nextInCarreras.setTarifa(null);
            iterCarreras.remove();
            this.entityManager.merge(nextInCarreras);
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
    * Support searching Tarifa entities with pagination
    */

   private int page;
   private long count;
   private List<Tarifa> pageItems;

   private Tarifa example = new Tarifa();

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

   public Tarifa getExample()
   {
      return this.example;
   }

   public void setExample(Tarifa example)
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
      Root<Tarifa> root = countCriteria.from(Tarifa.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Tarifa> criteria = builder.createQuery(Tarifa.class);
      root = criteria.from(Tarifa.class);
      TypedQuery<Tarifa> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Tarifa> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idTarifa = this.example.getIdTarifa();
      if (idTarifa != 0)
      {
         predicatesList.add(builder.equal(root.get("idTarifa"), idTarifa));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      String descripcion = this.example.getDescripcion();
      if (descripcion != null && !"".equals(descripcion))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("descripcion")), '%' + descripcion.toLowerCase() + '%'));
      }
      String flagEstado = this.example.getFlagEstado();
      if (flagEstado != null && !"".equals(flagEstado))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("flagEstado")), '%' + flagEstado.toLowerCase() + '%'));
      }
      String usuarioReg = this.example.getUsuarioReg();
      if (usuarioReg != null && !"".equals(usuarioReg))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("usuarioReg")), '%' + usuarioReg.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Tarifa> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Tarifa entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Tarifa> getAll()
   {

      CriteriaQuery<Tarifa> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Tarifa.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Tarifa.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TarifaBean ejbProxy = this.sessionContext.getBusinessObject(TarifaBean.class);

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

            return String.valueOf(((Tarifa) value).getIdTarifa());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Tarifa add = new Tarifa();

   public Tarifa getAdd()
   {
      return this.add;
   }

   public Tarifa getAdded()
   {
      Tarifa added = this.add;
      this.add = new Tarifa();
      return added;
   }
}

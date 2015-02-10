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

import com.sinapsistech.taxiWeb.model.Carrera;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Tarifa;
import com.sinapsistech.taxiWeb.model.Transaccion;
import com.sinapsistech.taxiWeb.model.Vehiculo;
import java.util.Iterator;

/**
 * Backing bean for Carrera entities.
 * <p/>
 * This class provides CRUD functionality for all Carrera entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CarreraBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Carrera entities
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

   private Carrera carrera;

   public Carrera getCarrera()
   {
      return this.carrera;
   }

   public void setCarrera(Carrera carrera)
   {
      this.carrera = carrera;
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
         this.carrera = this.example;
      }
      else
      {
         this.carrera = findById(getId());
      }
   }

   public Carrera findById(Integer id)
   {

      return this.entityManager.find(Carrera.class, id);
   }

   /*
    * Support updating and deleting Carrera entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.carrera);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.carrera);
            return "view?faces-redirect=true&id=" + this.carrera.getIdCarrera();
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
         Carrera deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getCarreras().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Tarifa tarifa = deletableEntity.getTarifa();
         tarifa.getCarreras().remove(deletableEntity);
         deletableEntity.setTarifa(null);
         this.entityManager.merge(tarifa);
         Vehiculo vehiculo = deletableEntity.getVehiculo();
         vehiculo.getCarreras().remove(deletableEntity);
         deletableEntity.setVehiculo(null);
         this.entityManager.merge(vehiculo);
         Iterator<Transaccion> iterTransaccions = deletableEntity.getTransaccions().iterator();
         for (; iterTransaccions.hasNext();)
         {
            Transaccion nextInTransaccions = iterTransaccions.next();
            nextInTransaccions.setCarrera(null);
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
    * Support searching Carrera entities with pagination
    */

   private int page;
   private long count;
   private List<Carrera> pageItems;

   private Carrera example = new Carrera();

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

   public Carrera getExample()
   {
      return this.example;
   }

   public void setExample(Carrera example)
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
      Root<Carrera> root = countCriteria.from(Carrera.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Carrera> criteria = builder.createQuery(Carrera.class);
      root = criteria.from(Carrera.class);
      TypedQuery<Carrera> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Carrera> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idCarrera = this.example.getIdCarrera();
      if (idCarrera != 0)
      {
         predicatesList.add(builder.equal(root.get("idCarrera"), idCarrera));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      Tarifa tarifa = this.example.getTarifa();
      if (tarifa != null)
      {
         predicatesList.add(builder.equal(root.get("tarifa"), tarifa));
      }
      Vehiculo vehiculo = this.example.getVehiculo();
      if (vehiculo != null)
      {
         predicatesList.add(builder.equal(root.get("vehiculo"), vehiculo));
      }
      String origen = this.example.getOrigen();
      if (origen != null && !"".equals(origen))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("origen")), '%' + origen.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Carrera> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Carrera entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Carrera> getAll()
   {

      CriteriaQuery<Carrera> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Carrera.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Carrera.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final CarreraBean ejbProxy = this.sessionContext.getBusinessObject(CarreraBean.class);

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

            return String.valueOf(((Carrera) value).getIdCarrera());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Carrera add = new Carrera();

   public Carrera getAdd()
   {
      return this.add;
   }

   public Carrera getAdded()
   {
      Carrera added = this.add;
      this.add = new Carrera();
      return added;
   }
}

package com.sinapsistech.taxiWeb.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
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
import javax.servlet.http.HttpServletRequest;

import com.sinapsistech.taxiWeb.model.Ciudad;
import com.sinapsistech.taxiWeb.model.Departamento;
import com.sinapsistech.taxiWeb.model.Usuario;

/**
 * Backing bean for Ciudad entities.
 * <p/>
 * This class provides CRUD functionality for all Ciudad entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CiudadBean implements Serializable
{

   private static final long serialVersionUID = 1L;
   FacesContext context = FacesContext.getCurrentInstance();
   ExternalContext externalContext = context.getExternalContext();
   HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

   /*
    * Support creating and retrieving Ciudad entities
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

   private Ciudad ciudad;

   public Ciudad getCiudad()
   {
      return this.ciudad;
   }

   public void setCiudad(Ciudad ciudad)
   {
      this.ciudad = ciudad;
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
         this.ciudad = this.example;
      }
      else
      {
         this.ciudad = findById(getId());
      }
   }

   public Ciudad findById(Integer id)
   {

      return this.entityManager.find(Ciudad.class, id);
   }

   /*
    * Support updating and deleting Ciudad entities
    */

   public String update()
   {
       System.out.println("Agarrando el contexto.");
       String nombre = request.getUserPrincipal().getName();
	   
	   this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	 System.out.println("Entro por ciudad nueva");
        	 this.ciudad.setFechaReg(new Date());
        	 this.ciudad.setUsuarioReg(nombre);
        	 this.ciudad.setFlagEstado("AC");
        	 this.entityManager.persist(this.ciudad);
            return "search?faces-redirect=true";
         }
         else
         {
        	 
        	System.out.println("Entro por ciudad actulizanda");
        	this.ciudad.setFechaMod(new Date());
        	this.ciudad.setUsuarioMod(nombre);
            this.entityManager.merge(this.ciudad);
            this.ciudad.setFlagEstado("AC");
            return "view?faces-redirect=true&id=" + this.ciudad.getIdCiudad();
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
       /*  Ciudad deletableEntity = findById(getId());
         Departamento departamento = deletableEntity.getDepartamento();
         departamento.getCiudads().remove(deletableEntity);
         deletableEntity.setDepartamento(null);
         this.entityManager.merge(departamento);
         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true"; */
    	  String nombre = request.getUserPrincipal().getName();
    	  Ciudad deletableEntity = findById(getId());
    	  deletableEntity.setFlagEstado("IN");
    	  deletableEntity.setUsuarioBorrado(nombre);
    	  deletableEntity.setFechaBorrado(new Date());
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
    * Support searching Ciudad entities with pagination
    */

   private int page;
   private long count;
   private List<Ciudad> pageItems;

   private Ciudad example = new Ciudad();

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

   public Ciudad getExample()
   {
      return this.example;
   }

   public void setExample(Ciudad example)
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
      Root<Ciudad> root = countCriteria.from(Ciudad.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Ciudad> criteria = builder.createQuery(Ciudad.class);
      root = criteria.from(Ciudad.class);
      TypedQuery<Ciudad> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Ciudad> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idCiudad = this.example.getIdCiudad();
      if (idCiudad != 0)
      {
         predicatesList.add(builder.equal(root.get("idCiudad"), idCiudad));
      }
      Departamento departamento = this.example.getDepartamento();
      if (departamento != null)
      {
         predicatesList.add(builder.equal(root.get("departamento"), departamento));
      }
      String nombre = this.example.getNombre();
      if (nombre != null && !"".equals(nombre))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombre")), '%' + nombre.toLowerCase() + '%'));
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

   public List<Ciudad> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Ciudad entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Ciudad> getAll()
   {

      CriteriaQuery<Ciudad> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Ciudad.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Ciudad.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final CiudadBean ejbProxy = this.sessionContext.getBusinessObject(CiudadBean.class);

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

            return String.valueOf(((Ciudad) value).getIdCiudad());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Ciudad add = new Ciudad();

   public Ciudad getAdd()
   {
      return this.add;
   }

   public Ciudad getAdded()
   {
      Ciudad added = this.add;
      this.add = new Ciudad();
      return added;
   }
}

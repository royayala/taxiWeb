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

import com.sinapsistech.taxiWeb.model.Departamento;
import com.sinapsistech.taxiWeb.model.TipoPersona;
import com.sinapsistech.taxiWeb.model.Persona;

import java.util.Iterator;

/**
 * Backing bean for TipoPersona entities.
 * <p/>
 * This class provides CRUD functionality for all TipoPersona entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TipoPersonaBean implements Serializable
{

   private static final long serialVersionUID = 1L;
   FacesContext context = FacesContext.getCurrentInstance();
   ExternalContext externalContext = context.getExternalContext();
   HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

   /*
    * Support creating and retrieving TipoPersona entities
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

   private TipoPersona tipoPersona;

   public TipoPersona getTipoPersona()
   {
      return this.tipoPersona;
   }

   public void setTipoPersona(TipoPersona tipoPersona)
   {
      this.tipoPersona = tipoPersona;
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
         this.tipoPersona = this.example;
      }
      else
      {
         this.tipoPersona = findById(getId());
      }
   }

   public TipoPersona findById(Integer id)
   {

      return this.entityManager.find(TipoPersona.class, id);
   }

   /*
    * Support updating and deleting TipoPersona entities
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
        	 System.out.println("Entro por TipoPersona nuevo");
        	 this.tipoPersona.setFechaReg(new Date());
        	 this.tipoPersona.setUsuarioReg(nombre);
        	 this.tipoPersona.setFlagEstado("AC");
            this.entityManager.persist(this.tipoPersona);
            return "search?faces-redirect=true";
         }
         else
         {
        	 System.out.println("Entro por TipoPersona actualizado");
        	 this.tipoPersona.setFechaMod(new Date());
        	 this.tipoPersona.setUsuarioMod(nombre);
        	 this.tipoPersona.setFlagEstado("AC");
        	 this.entityManager.merge(this.tipoPersona);
            return "view?faces-redirect=true&id=" + this.tipoPersona.getIdTipoPersona();
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
         /*TipoPersona deletableEntity = findById(getId());
         Iterator<Persona> iterPersonas = deletableEntity.getPersonas().iterator();
         for (; iterPersonas.hasNext();)
         {
            Persona nextInPersonas = iterPersonas.next();
            nextInPersonas.setTipoPersona(null);
            iterPersonas.remove();
            this.entityManager.merge(nextInPersonas);
         }
         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";*/
    	  String nombre = request.getUserPrincipal().getName();
    	  TipoPersona deletableEntity = findById(getId());
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
    * Support searching TipoPersona entities with pagination
    */

   private int page;
   private long count;
   private List<TipoPersona> pageItems;

   private TipoPersona example = new TipoPersona();

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

   public TipoPersona getExample()
   {
      return this.example;
   }

   public void setExample(TipoPersona example)
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
      Root<TipoPersona> root = countCriteria.from(TipoPersona.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<TipoPersona> criteria = builder.createQuery(TipoPersona.class);
      root = criteria.from(TipoPersona.class);
      TypedQuery<TipoPersona> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<TipoPersona> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idTipoPersona = this.example.getIdTipoPersona();
      if (idTipoPersona != 0)
      {
         predicatesList.add(builder.equal(root.get("idTipoPersona"), idTipoPersona));
      }
      String nombre = this.example.getNombre();
      if (nombre != null && !"".equals(nombre))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombre")), '%' + nombre.toLowerCase() + '%'));
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

   public List<TipoPersona> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back TipoPersona entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<TipoPersona> getAll()
   {

      CriteriaQuery<TipoPersona> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(TipoPersona.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(TipoPersona.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TipoPersonaBean ejbProxy = this.sessionContext.getBusinessObject(TipoPersonaBean.class);

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

            return String.valueOf(((TipoPersona) value).getIdTipoPersona());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private TipoPersona add = new TipoPersona();

   public TipoPersona getAdd()
   {
      return this.add;
   }

   public TipoPersona getAdded()
   {
      TipoPersona added = this.add;
      this.add = new TipoPersona();
      return added;
   }
}

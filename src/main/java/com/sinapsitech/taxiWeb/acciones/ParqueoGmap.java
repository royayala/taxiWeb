package com.sinapsitech.taxiWeb.acciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
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
import com.sinapsistech.taxiWeb.model.Parqueo;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Vehiculo;
import com.sinapsistech.taxiWeb.model.VehiculoParqueo;
import com.sinapsistech.taxiWeb.view.ParqueoBean;

import java.util.Iterator;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
/**
 * Backing bean for Parqueo entities.
 * <p/>
 * This class provides CRUD functionality for all Parqueo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ParqueoGmap implements Serializable
{

   private static final long serialVersionUID = 1L;
   FacesContext context = FacesContext.getCurrentInstance();
   ExternalContext externalContext = context.getExternalContext();
   HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

   /*
    * Support creating and retrieving Parqueo entities
    */
   
   @Inject ParqueoBean parqueoBean;

   private Integer id;

   private MapModel simpleModel;
   
   List<Parqueo> listaParqueo;
   
   @PostConstruct
   public void init() {
	   
	   //obtener la lista de parqueos
	   listaParqueo=parqueoBean.getAll();
	   
       simpleModel = new DefaultMapModel();
         
       // interactuar en la lista de parqueos para adicionarlo la simpleModel
       
       for(Iterator<Parqueo> it = listaParqueo.iterator(); it.hasNext();) {
   		   
   		 Parqueo objetoParqueo = it.next();
       
       //Shared coordinates
       LatLng coord1 = new LatLng(new Double(objetoParqueo.getLatitud()),new Double( objetoParqueo.getLongitud()));
      
         
       //Basic marker
       simpleModel.addOverlay(new Marker(coord1, objetoParqueo.getDireccion()));
       
       }
       
   }
 
   public MapModel getSimpleModel() {
       return simpleModel;
   }
   
   public Integer getId()
   {
      return this.id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   private Parqueo parqueo;

   public Parqueo getParqueo()
   {
      return this.parqueo;
   }

   public void setParqueo(Parqueo parqueo)
   {
      this.parqueo = parqueo;
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
         this.parqueo = this.example;
      }
      else
      {
         this.parqueo = findById(getId());
      }
   }

   public Parqueo findById(Integer id)
   {

      return this.entityManager.find(Parqueo.class, id);
   }

   /*
    * Support updating and deleting Parqueo entities
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
        	System.out.println("Entro por parqueo nuevo");
        	
        	this.parqueo.setFechaReg(new Date());
        	this.parqueo.setUsuarioReg(nombre);
        	this.parqueo.setFlagEstado("AC");
            this.entityManager.persist(this.parqueo);
            return "search?faces-redirect=true";
         }
         else
         {
        	System.out.println("Entro por parqueo nuevo");
         	
         	this.parqueo.setFechaMod(new Date());
         	this.parqueo.setUsuarioMod(nombre);
         	this.parqueo.setFlagEstado("AC");
            this.entityManager.merge(this.parqueo);
            return "view?faces-redirect=true&id=" + this.parqueo.getIdParqueo();
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
        /* Parqueo deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getParqueos().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Iterator<VehiculoParqueo> iterVehiculoParqueos = deletableEntity.getVehiculoParqueos().iterator();
         for (; iterVehiculoParqueos.hasNext();)
         {
            VehiculoParqueo nextInVehiculoParqueos = iterVehiculoParqueos.next();
            nextInVehiculoParqueos.setParqueo(null);
            iterVehiculoParqueos.remove();
            this.entityManager.merge(nextInVehiculoParqueos);
         }
         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";*/
    	  String nombre = request.getUserPrincipal().getName();
    	  Parqueo deletableEntity = findById(getId());
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
    * Support searching Parqueo entities with pagination
    */

   private int page;
   private long count;
   private List<Parqueo> pageItems;

   private Parqueo example = new Parqueo();

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

   public Parqueo getExample()
   {
      return this.example;
   }

   public void setExample(Parqueo example)
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
      Root<Parqueo> root = countCriteria.from(Parqueo.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Parqueo> criteria = builder.createQuery(Parqueo.class);
      root = criteria.from(Parqueo.class);
      TypedQuery<Parqueo> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Parqueo> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idParqueo = this.example.getIdParqueo();
      if (idParqueo != 0)
      {
         predicatesList.add(builder.equal(root.get("idParqueo"), idParqueo));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      String direccion = this.example.getDireccion();
      if (direccion != null && !"".equals(direccion))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("direccion")), '%' + direccion.toLowerCase() + '%'));
      }
      int idCiudad = this.example.getIdCiudad();
      if (idCiudad != 0)
      {
         predicatesList.add(builder.equal(root.get("idCiudad"), idCiudad));
      }
      String latitud = this.example.getLatitud();
      if (latitud != null && !"".equals(latitud))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("latitud")), '%' + latitud.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Parqueo> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Parqueo entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Parqueo> getAll()
   {

      CriteriaQuery<Parqueo> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Parqueo.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Parqueo.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final ParqueoGmap ejbProxy = this.sessionContext.getBusinessObject(ParqueoGmap.class);

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

            return String.valueOf(((Parqueo) value).getIdParqueo());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Parqueo add = new Parqueo();

   public Parqueo getAdd()
   {
      return this.add;
   }

   public Parqueo getAdded()
   {
      Parqueo added = this.add;
      this.add = new Parqueo();
      return added;
   }
}

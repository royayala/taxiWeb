package com.sinapsistech.taxiWeb.model;

// Generated Feb 9, 2015 11:05:15 AM by Hibernate Tools 4.3.1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * DetallaPersonaServicio generated by hbm2java
 */
@Entity
@Table(name = "detalla_persona_servicio"
      , schema = "public")
public class DetallaPersonaServicio implements java.io.Serializable
{

   private int idDetPersonaServicio;
   private Compania compania;
   private PersonaServicio personaServicio;
   private Vehiculo vehiculo;
   private Date fechaDetPersonaServicio;
   private Date horaInicio;
   private Date horaFin;
   private String observaciones;
   private String direccionInicion;
   private String direccionFin;
   private boolean flagCobrar;
   private Double monto;
   private boolean flagReprogramado;
   private Integer reprogramadoReferencia;
   private String flagEstado;
   private Date fechaReg;
   private String usuarioReg;
   private Date fechaMod;
   private String usuarioMod;
   private Date fechaBorrado;
   private String usuarioBorrado;

   public DetallaPersonaServicio()
   {
   }

   public DetallaPersonaServicio(int idDetPersonaServicio, Compania compania, PersonaServicio personaServicio, Vehiculo vehiculo, Date fechaDetPersonaServicio, Date horaInicio, boolean flagCobrar, boolean flagReprogramado)
   {
      this.idDetPersonaServicio = idDetPersonaServicio;
      this.compania = compania;
      this.personaServicio = personaServicio;
      this.vehiculo = vehiculo;
      this.fechaDetPersonaServicio = fechaDetPersonaServicio;
      this.horaInicio = horaInicio;
      this.flagCobrar = flagCobrar;
      this.flagReprogramado = flagReprogramado;
   }

   public DetallaPersonaServicio(int idDetPersonaServicio, Compania compania, PersonaServicio personaServicio, Vehiculo vehiculo, Date fechaDetPersonaServicio, Date horaInicio, Date horaFin, String observaciones, String direccionInicion, String direccionFin, boolean flagCobrar, Double monto, boolean flagReprogramado, Integer reprogramadoReferencia, String flagEstado, Date fechaReg, String usuarioReg, Date fechaMod, String usuarioMod, Date fechaBorrado, String usuarioBorrado)
   {
      this.idDetPersonaServicio = idDetPersonaServicio;
      this.compania = compania;
      this.personaServicio = personaServicio;
      this.vehiculo = vehiculo;
      this.fechaDetPersonaServicio = fechaDetPersonaServicio;
      this.horaInicio = horaInicio;
      this.horaFin = horaFin;
      this.observaciones = observaciones;
      this.direccionInicion = direccionInicion;
      this.direccionFin = direccionFin;
      this.flagCobrar = flagCobrar;
      this.monto = monto;
      this.flagReprogramado = flagReprogramado;
      this.reprogramadoReferencia = reprogramadoReferencia;
      this.flagEstado = flagEstado;
      this.fechaReg = fechaReg;
      this.usuarioReg = usuarioReg;
      this.fechaMod = fechaMod;
      this.usuarioMod = usuarioMod;
      this.fechaBorrado = fechaBorrado;
      this.usuarioBorrado = usuarioBorrado;
   }

   @Id
   @Column(name = "id_det_persona_servicio", unique = true, nullable = false)
   public int getIdDetPersonaServicio()
   {
      return this.idDetPersonaServicio;
   }

   public void setIdDetPersonaServicio(int idDetPersonaServicio)
   {
      this.idDetPersonaServicio = idDetPersonaServicio;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_cia", nullable = false)
   public Compania getCompania()
   {
      return this.compania;
   }

   public void setCompania(Compania compania)
   {
      this.compania = compania;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_persona_servicio", nullable = false)
   public PersonaServicio getPersonaServicio()
   {
      return this.personaServicio;
   }

   public void setPersonaServicio(PersonaServicio personaServicio)
   {
      this.personaServicio = personaServicio;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_vehiculo", nullable = false)
   public Vehiculo getVehiculo()
   {
      return this.vehiculo;
   }

   public void setVehiculo(Vehiculo vehiculo)
   {
      this.vehiculo = vehiculo;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "fecha_det_persona_servicio", nullable = false, length = 29)
   public Date getFechaDetPersonaServicio()
   {
      return this.fechaDetPersonaServicio;
   }

   public void setFechaDetPersonaServicio(Date fechaDetPersonaServicio)
   {
      this.fechaDetPersonaServicio = fechaDetPersonaServicio;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "hora_inicio", nullable = false, length = 29)
   public Date getHoraInicio()
   {
      return this.horaInicio;
   }

   public void setHoraInicio(Date horaInicio)
   {
      this.horaInicio = horaInicio;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "hora_fin", length = 29)
   public Date getHoraFin()
   {
      return this.horaFin;
   }

   public void setHoraFin(Date horaFin)
   {
      this.horaFin = horaFin;
   }

   @Column(name = "observaciones")
   public String getObservaciones()
   {
      return this.observaciones;
   }

   public void setObservaciones(String observaciones)
   {
      this.observaciones = observaciones;
   }

   @Column(name = "direccion_inicion", length = 200)
   public String getDireccionInicion()
   {
      return this.direccionInicion;
   }

   public void setDireccionInicion(String direccionInicion)
   {
      this.direccionInicion = direccionInicion;
   }

   @Column(name = "direccion_fin", length = 200)
   public String getDireccionFin()
   {
      return this.direccionFin;
   }

   public void setDireccionFin(String direccionFin)
   {
      this.direccionFin = direccionFin;
   }

   @Column(name = "flag_cobrar", nullable = false)
   public boolean isFlagCobrar()
   {
      return this.flagCobrar;
   }

   public void setFlagCobrar(boolean flagCobrar)
   {
      this.flagCobrar = flagCobrar;
   }

   @Column(name = "monto", precision = 17, scale = 17)
   public Double getMonto()
   {
      return this.monto;
   }

   public void setMonto(Double monto)
   {
      this.monto = monto;
   }

   @Column(name = "flag_reprogramado", nullable = false)
   public boolean isFlagReprogramado()
   {
      return this.flagReprogramado;
   }

   public void setFlagReprogramado(boolean flagReprogramado)
   {
      this.flagReprogramado = flagReprogramado;
   }

   @Column(name = "reprogramado_referencia")
   public Integer getReprogramadoReferencia()
   {
      return this.reprogramadoReferencia;
   }

   public void setReprogramadoReferencia(Integer reprogramadoReferencia)
   {
      this.reprogramadoReferencia = reprogramadoReferencia;
   }

   @Column(name = "flag_estado", length = 2)
   public String getFlagEstado()
   {
      return this.flagEstado;
   }

   public void setFlagEstado(String flagEstado)
   {
      this.flagEstado = flagEstado;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "fecha_reg", length = 29)
   public Date getFechaReg()
   {
      return this.fechaReg;
   }

   public void setFechaReg(Date fechaReg)
   {
      this.fechaReg = fechaReg;
   }

   @Column(name = "usuario_reg", length = 30)
   public String getUsuarioReg()
   {
      return this.usuarioReg;
   }

   public void setUsuarioReg(String usuarioReg)
   {
      this.usuarioReg = usuarioReg;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "fecha_mod", length = 29)
   public Date getFechaMod()
   {
      return this.fechaMod;
   }

   public void setFechaMod(Date fechaMod)
   {
      this.fechaMod = fechaMod;
   }

   @Column(name = "usuario_mod", length = 30)
   public String getUsuarioMod()
   {
      return this.usuarioMod;
   }

   public void setUsuarioMod(String usuarioMod)
   {
      this.usuarioMod = usuarioMod;
   }

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "fecha_borrado", length = 29)
   public Date getFechaBorrado()
   {
      return this.fechaBorrado;
   }

   public void setFechaBorrado(Date fechaBorrado)
   {
      this.fechaBorrado = fechaBorrado;
   }

   @Column(name = "usuario_borrado", length = 30)
   public String getUsuarioBorrado()
   {
      return this.usuarioBorrado;
   }

   public void setUsuarioBorrado(String usuarioBorrado)
   {
      this.usuarioBorrado = usuarioBorrado;
   }

}

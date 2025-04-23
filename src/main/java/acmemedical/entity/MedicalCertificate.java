/********************************************************************************************************
 * File:  MedicalCertificate.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

@SuppressWarnings("unused")

/**
 * The persistent class for the medical_certificate database table.
 */
//TODO MC01 - Add the missing annotations.
//TODO MC02 - Do we need a mapped super class?  If so, which one?
@Entity
@Table(name = "medical_certificate")
@Access(AccessType.FIELD)
@AttributeOverride(name = "id", column = @Column(name = "certificate_id"))
@NamedQuery(name = MedicalCertificate.ID_CARD_QUERY_NAME, query = "SELECT mc FROM MedicalCertificate mc WHERE mc.id = :param1")
@NamedQuery(name = MedicalCertificate.ALL_CTF_QUERY_NAME, query = "SELECT mc FROM MedicalCertificate mc")
public class MedicalCertificate extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public static final String ID_CARD_QUERY_NAME = "MedicalCertificate.findById";

	public static final String ALL_CTF_QUERY_NAME = "MedicalCertificate.findAll";
	
	// TODO MC03 - Add annotations for 1:1 mapping.  What should be the cascade and fetch types?
	@OneToOne
	@JoinColumn(name = "training_id", referencedColumnName = "training_id", nullable = false)
	@JsonBackReference("medicalCertificateMedicalTrainings")	
	private MedicalTraining medicalTraining;

	// TODO MC04 - Add annotations for M:1 mapping.  What should be the cascade and fetch types?
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "physician_id", referencedColumnName = "id", nullable = false)
	@JsonBackReference("physicianMedicalCertificates")
	private Physician owner;

	// TODO MC05 - Add annotations.
	@Column(name = "signed", nullable = false)
	private byte signed;

	public MedicalCertificate() {
		super();
	}
	
	public MedicalCertificate(MedicalTraining medicalTraining, Physician owner, byte signed) {
		this();
		this.medicalTraining = medicalTraining;
		this.owner = owner;
		this.signed = signed;
	}

	public MedicalTraining getMedicalTraining() {
		return medicalTraining;
	}

	public void setMedicalTraining(MedicalTraining medicalTraining) {
		this.medicalTraining = medicalTraining;
	}

	public Physician getOwner() {
		return owner;
	}

	public void setOwner(Physician owner) {
		this.owner = owner;
	}

	public byte getSigned() {
		return signed;
	}

	/* duplicated, commented this one temporarily
	public void setSigned(byte signed) {
		this.signed = signed;
	}
	*/
	
	public void setSigned(boolean signed) {
		this.signed = (byte) (signed ? 0b0001 : 0b0000);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
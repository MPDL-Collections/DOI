package de.mpg.mdpl.doi.controller;

import java.util.List;

import de.mpg.mdpl.doi.exception.DoiAlreadyExistsException;
import de.mpg.mdpl.doi.exception.DoiNotFoundException;
import de.mpg.mdpl.doi.exception.DoiRegisterException;
import de.mpg.mdpl.doi.exception.DoxiException;
import de.mpg.mdpl.doi.exception.MetadataInvalidException;
import de.mpg.mdpl.doi.model.DOI;

public interface DoiControllerInterface {

	/**
	 * @return xml
	 * @param doi
	 */
	public DOI getDOI(String doi);
	
	public List<DOI> getDOIList() throws DoxiException;

	public DOI createDOI(String doi, String url, String metadataXml) throws DoxiException, DoiAlreadyExistsException, MetadataInvalidException, DoiRegisterException;
	
	public DOI createDOIAutoGenerated(String url, String metadataXml) throws DoxiException, DoiAlreadyExistsException, MetadataInvalidException, DoiRegisterException;
	
	public DOI createDOIKnownSuffix(String suffix, String url, String metadataXml) throws DoxiException, DoiAlreadyExistsException, MetadataInvalidException, DoiRegisterException;

	public DOI updateDOI(String doi, String url, String metadataXml) throws DoxiException, DoiNotFoundException, MetadataInvalidException, DoiRegisterException;
	
	public void inactivateDOI(String doi) throws DoxiException;

}
package com.elolympus.services.services;

import com.elolympus.data.Empresa.Empresa;
import com.elolympus.services.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends AbstractCrudService<Empresa, EmpresaRepository> {

    private final EmpresaRepository repository;

    @Autowired
    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected EmpresaRepository getRepository() {
        return repository;
    }

    @Override
    protected String getTableName() {
        return "empresa";
    }

    @Override
    protected String getEntityName() {
        return "Empresa";
    }

    @Override
    protected void copyEditableFields(Empresa source, Empresa target) {
        target.setDireccion(source.getDireccion());
        target.setFolderTemps(source.getFolderTemps());
        target.setFolderReports(source.getFolderReports());
        target.setAllowBuyWithoutStock(source.getAllowBuyWithoutStock());
        target.setRequireSalesPin(source.getRequireSalesPin());
        target.setDocumentoTipoXdefecto(source.getDocumentoTipoXdefecto());
        target.setLogoEnterprise(source.getLogoEnterprise());
        target.setLogoWidth(source.getLogoWidth());
        target.setCommercialName(source.getCommercialName());
    }
}

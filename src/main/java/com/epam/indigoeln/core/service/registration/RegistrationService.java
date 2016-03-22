package com.epam.indigoeln.core.service.registration;

import com.epam.indigoeln.core.model.Component;
import com.epam.indigoeln.core.model.Compound;
import com.epam.indigoeln.core.repository.component.ComponentRepository;
import com.epam.indigoeln.core.repository.registration.RegistrationException;
import com.epam.indigoeln.core.repository.registration.RegistrationRepository;
import com.epam.indigoeln.core.repository.registration.RegistrationRepositoryInfo;
import com.epam.indigoeln.core.repository.registration.RegistrationStatus;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private static final String DEFAULT_SIMILARITY = "tanimoto";

    @Autowired
    private List<RegistrationRepository> repositories;

    @Autowired
    private ComponentRepository componentRepository;

    public List<RegistrationRepositoryInfo> getRepositoriesInfo() {
        return repositories.stream().map(RegistrationRepository::getInfo).collect(Collectors.toList());
    }

    private RegistrationRepository getRegistrationRepository(String id) throws RegistrationException {
        Optional<RegistrationRepository> optional = repositories.stream().filter(r -> r.getInfo().getId().equals(id)).findFirst();
        if (!optional.isPresent()) {
            throw new RegistrationException("Unknown repository ID: " + id);
        }
        return optional.get();
    }

    private Map<BatchSummary, Component> getBatches(Supplier<Collection<Component>> supplier, Predicate<BatchSummary> predicate) {
        Map<BatchSummary, Component> batchesMap = new HashMap<>();
        Collection<Component> components = supplier.get();
        components.forEach(c -> {
            BasicDBList batches = (BasicDBList) c.getContent().get("batches");
            batches.forEach(b -> {
                BasicDBObject batch = (BasicDBObject) b;
                batchesMap.put(new BatchSummary(batch), c);
            });
        });
        return batchesMap.entrySet().stream().filter(e -> predicate.test(e.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Long register(String id, List<String> fullBatchNumbers) throws RegistrationException {

        Map<BatchSummary, Component> batches = getBatches(
                () -> componentRepository.findBatchSummariesByFullBatchNumbers(fullBatchNumbers),
                b -> fullBatchNumbers.contains(b.getFullNbkBatch()));

        if (fullBatchNumbers.size() != batches.size()) {
            Set<String> foundFullNbkBatches = batches.keySet().stream().map(BatchSummary::getFullNbkBatch).collect(Collectors.toSet());
            throw new RegistrationException("Unable to find batches for registration: " +
                    CollectionUtils.subtract(fullBatchNumbers, foundFullNbkBatches));
        }

        Optional<BatchSummary> inProgressOpt = batches.keySet().stream().filter(
                b -> RegistrationStatus.Status.IN_PROGRESS.toString().equals(b.getRegistrationStatus())
        ).findFirst();
        if (inProgressOpt.isPresent()) {
            throw new RegistrationException("Batch " + inProgressOpt.get().getFullNbkBatch() + " is already on registration.");
        }

        List<Compound> compounds = batches.keySet().stream().map(b -> convert(b.getDelegate())).collect(Collectors.toList());
        Long jobId = getRegistrationRepository(id).register(compounds);

        batches.keySet().stream().forEach(b -> {
            b.setRegistrationStatus(RegistrationStatus.Status.IN_PROGRESS.toString());
            b.setRegistrationJobId(jobId);
        });

        componentRepository.save(new HashSet<>(batches.values()));

        return jobId;
    }

    public RegistrationStatus getStatus(String id, long jobId) throws RegistrationException {
        RegistrationStatus registrationStatus = getRegistrationRepository(id).getRegisterJobStatus(jobId);
        if (!registrationStatus.getStatus().equals(RegistrationStatus.Status.IN_PROGRESS)) {

            Map<BatchSummary, Component> batches = getBatches(
                    () -> componentRepository.findBatchSummariesByRegistrationJobId(jobId),
                    b -> {
                        Long registrationJobId = b.getRegistrationJobId();
                        return registrationJobId != null && registrationJobId == jobId;
                    });

            batches.keySet().stream().forEach(b -> {
                b.setRegistrationStatus(registrationStatus.getStatus().toString());
            });

            componentRepository.save(new HashSet<>(batches.values()));

        }
        return registrationStatus;
    }

    public List<Compound> getRegisteredCompounds(String id, Long jobId) throws RegistrationException {
        return getRegistrationRepository(id).getRegisteredCompounds(jobId);
    }

    public List<Integer> searchSubstructure(String id, String structure, String searchOption) throws RegistrationException {
        return getRegistrationRepository(id).searchSub(structure, searchOption);
    }

    public List<Integer> searchSimilarity(String id, String structure, String searchOption) throws RegistrationException {
        return getRegistrationRepository(id).searchSim(structure, DEFAULT_SIMILARITY, Double.parseDouble(searchOption) / 100, (double) 1);
    }

    public List<Integer> searchSmarts(String id, String structure) throws RegistrationException {
        return getRegistrationRepository(id).searchSmarts(structure);
    }

    public List<Integer> searchExact(String id, String structure, String searchOption) throws RegistrationException {
        return getRegistrationRepository(id).searchExact(structure, searchOption);
    }

    private Compound convert(BasicDBObject batch) {
        Compound result = new Compound();
        //TODO: replace FIELD with real values
        result.setChemicalName(batch.getString("FIELD"));
        result.setCompoundNo(batch.getString("virtualCompoundId"));
        result.setConversationalBatchNo(batch.getString("conversationalBatch"));
        result.setBatchNo(batch.getString("fullNbkBatch"));
        result.setCasNo(batch.getString("FIELD"));
        result.setStructure(batch.getString("molfile"));
        result.setMolFormula(batch.getString("molFormula"));
        result.setStereoisomerCode(batch.getString("stereoisomer"));
        result.setSaltCode(batch.getString("FIELD"));
        if (batch.containsField("FIELD")) {
            result.setSaltEquivs(batch.getDouble("FIELD"));
        }
        result.setComment(batch.getString("structureComments"));
        result.setHazardComment(batch.getString("hazards"));
        result.setStorageComment(batch.getString("FIELD"));
        result.setRegistrationStatus(batch.getString("registrationStatus"));

        return result;
    }

    private class BatchSummary {

        private BasicDBObject delegate;

        public BatchSummary(BasicDBObject delegate) {
            this.delegate = delegate;
        }

        public BasicDBObject getDelegate() {
            return delegate;
        }

        public String getFullNbkBatch() {
            return delegate.getString("fullNbkBatch");
        }

        public String getRegistrationStatus() {
            return delegate.getString("registrationStatus");
        }

        public void setRegistrationStatus(String registrationStatus) {
            delegate.put("registrationStatus", registrationStatus);
        }

        public Long getRegistrationJobId() {
            long registrationJobId = delegate.getLong("registrationJobId", -1);
            return registrationJobId == -1 ? null : registrationJobId;
        }

        public void setRegistrationJobId(long registrationJobId) {
            delegate.put("registrationJobId", registrationJobId);
        }

    }

}

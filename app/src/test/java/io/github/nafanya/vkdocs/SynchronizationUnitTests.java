package io.github.nafanya.vkdocs;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

@RunWith(MockitoJUnitRunner.class)
public class SynchronizationUnitTests {
/*
    private EventBus dummyEventBus = new DummyEventBus();

    private DocumentRepository documentRepository;

    public enum EventType {ADD_SERVER, DELETE_SERVER, DELETE_DOCUMENT, SYNCHRONIZE};
    private DbMapper mapper = new DbMapper();
    private InternetService randomIS = new RandomInternetService();

    @Test
    public void syncTest() throws Exception {
        final SyncNetworkRepository networkRepository = new SyncNetworkRepository(100, 500, randomIS);
        final DatabaseRepository databaseRepository = new InMemoryDatabaseRepository(mapper, 100);
        documentRepository = new DocumentRepositoryImpl(databaseRepository, networkRepository);
        final int ITERATIONS = 400;

        final Set<VKApiDocument> correct = new TreeSet<>(new Comparator<VKApiDocument>() {
            @Override
            public int compare(VKApiDocument lhs, VKApiDocument rhs) {
                return lhs.getId() - rhs.getId();
            }
        });
        for (VKApiDocument d: networkRepository.getMyDocuments())
            correct.add(d);

        for (int iteration = 1; iteration <= ITERATIONS; ++iteration) {
            if (iteration % 100 == 0) {
                System.out.println("iteration: " + iteration + " from " + ITERATIONS);
                System.out.println("db reposritory size = " + databaseRepository.getMyDocuments().size());
                System.out.println("network repository size = " + networkRepository.getMyDocuments().size());
                System.out.println("========================");
            }

            int type = Utils.randInt(4);
            EventType event = EventType.values()[type];

            if (event == EventType.ADD_SERVER) {
                VKApiDocument doc = Utils.randVkApiDocument();
                correct.add(doc);
                networkRepository.addSS(doc);
            } else if (event == EventType.DELETE_SERVER) {
                List<VKApiDocument> netDocs = networkRepository.getMyDocuments();
                VKApiDocument vkApiDocument = netDocs.get(Utils.randInt(netDocs.size()));
                correct.remove(vkApiDocument);
                networkRepository.deleteSS(vkApiDocument);
            } else if (event == EventType.DELETE_DOCUMENT && databaseRepository.getMyDocuments().size() != 0) {
                List<VKDocumentEntity> dbDocs = databaseRepository.getMyDocuments();
                VKDocumentEntity vkDocumentEntity = dbDocs.get(Utils.randInt(dbDocs.size()));
                documentRepository.delete(mapper.transform(vkDocumentEntity));

                correct.remove(mapper.transform(vkDocumentEntity));
            } else if (event == EventType.SYNCHRONIZE) {
                final Reference<AssertionFailedError> exception = new Reference<>();

                new LoadMyDocuments(Schedulers.immediate(),
                        Schedulers.immediate(),
                        dummyEventBus, true, documentRepository).
                        execute(new DefaultSubscriber<List<VkDocument>>() {
                            @Override
                            public void onNext(List<VkDocument> documents) {
                                Assert.assertTrue(correct.size() == documents.size() && correct.containsAll(documents));
                                //List<VKApiDocument> netDocs = networkRepository.getMyDocuments();
                                //Assert.assertTrue(correct.size() == netDocs.size() && correct.containsAll(netDocs));
                                List<VkDocument> dbDocs = mapper.transform(databaseRepository.getMyDocuments());
                                Assert.assertTrue(correct.size() == dbDocs.size() && correct.containsAll(dbDocs));
                            }

                            @Override
                            public void onError(Throwable e) {
                                exception.value = (AssertionFailedError)e;
                            }
                        });
                if (exception.value != null)
                    throw exception.value;
            }
        }
    }*/
}
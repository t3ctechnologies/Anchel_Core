package com.t3c.anchel.batches;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.batches.GenericBatch;
import com.t3c.anchel.core.business.service.DocumentEntryBusinessService;
import com.t3c.anchel.core.dao.FileDataStore;
import com.t3c.anchel.core.domain.constants.FileSizeUnit;
import com.t3c.anchel.core.domain.constants.LinShareTestConstants;
import com.t3c.anchel.core.domain.constants.Policies;
import com.t3c.anchel.core.domain.constants.TimeUnit;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.Document;
import com.t3c.anchel.core.domain.entities.DocumentEntry;
import com.t3c.anchel.core.domain.entities.FileSizeUnitClass;
import com.t3c.anchel.core.domain.entities.Functionality;
import com.t3c.anchel.core.domain.entities.Policy;
import com.t3c.anchel.core.domain.entities.StringValueFunctionality;
import com.t3c.anchel.core.domain.entities.TimeUnitClass;
import com.t3c.anchel.core.domain.entities.UnitValueFunctionality;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.job.quartz.BatchRunContext;
import com.t3c.anchel.core.job.quartz.ResultContext;
import com.t3c.anchel.core.repository.AccountRepository;
import com.t3c.anchel.core.repository.DocumentEntryRepository;
import com.t3c.anchel.core.repository.DocumentRepository;
import com.t3c.anchel.core.repository.FunctionalityRepository;
import com.t3c.anchel.core.repository.ThreadEntryRepository;
import com.t3c.anchel.core.repository.UserRepository;
import com.t3c.anchel.core.runner.BatchRunner;
import com.t3c.anchel.core.service.DocumentEntryService;
import com.t3c.anchel.core.upgrade.v2_0.Sha256SumUpgradeTaskImpl;
import com.t3c.anchel.mongo.repository.UpgradeTaskLogMongoRepository;
import com.t3c.anchel.service.LoadingServiceTestDatas;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class ShaBatchImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(ShaBatchImplTest.class);

	@Autowired
	private BatchRunner batchRunner;

	private GenericBatch shaSumBatch;

	/*
	 * beans related to shaSumBatch
	 */
	@Qualifier("accountRepository")
	@Autowired
	private AccountRepository<Account> accountRepository;

	@Autowired
	private ThreadEntryRepository threadEntryRepository;

	@Autowired
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Autowired
	private UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository;

	@Autowired
	private FileDataStore fileDataStore;


	@Autowired
	private DocumentRepository documentRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private DocumentEntryService documentEntryService;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	private LoadingServiceTestDatas datas;

	private DocumentEntry aDocumentEntry;

	private DocumentEntry bDocumentEntry;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	private User jane;
	private final InputStream stream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("jackRabbit.properties");
	private final String fileName2 = "jackRabbit.properties";
	private final String comment2 = "file description sample";
	private final InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("OPTIONAL-springContext-jackRabbit.xml");
	private final String fileName1 = "linshare-default.properties";
	private final String comment1 = "file description default";

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-sha.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
		shaSumBatch = new Sha256SumUpgradeTaskImpl(accountRepository, documentRepository, fileDataStore, threadEntryRepository, upgradeTaskLogMongoRepository, documentEntryBusinessService);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testLaunch() throws BusinessException, JobExecutionException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(shaSumBatch);
		Assert.assertTrue("At least one batch failed.", batchRunner.execute(batches));
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testShaGetAll() throws BusinessException, JobExecutionException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		BatchRunContext batchRunContext = new BatchRunContext();
		Account actor = jane;
		createFunctionalities();
		File tempFile1 = File.createTempFile("linshare-test-", ".tmp");
		File tempFile2 = File.createTempFile("linshare-test-2", ".tmp");
		List<String> l = Lists.newArrayList();
		IOUtils.transferTo(stream1, tempFile1);
		IOUtils.transferTo(stream2, tempFile2);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile1, fileName1, comment1, false, null);
		bDocumentEntry = documentEntryService.create(actor, actor, tempFile2, fileName2, comment2, false, null);
		Assert.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) != null);
		Assert.assertTrue(documentEntryRepository.findById(bDocumentEntry.getUuid()) != null);
		aDocumentEntry.getDocument().setSha256sum("UNDEFINED");
		bDocumentEntry.getDocument().setSha256sum("UNDEFINED");
		l = shaSumBatch.getAll(batchRunContext);
		Assert.assertEquals(l.size(), 2);
	}

	@Test
	public void testSha256Batch() throws BusinessException, JobExecutionException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		BatchRunContext batchRunContext = new BatchRunContext();
		Account actor = jane;
		createFunctionalities();
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		File tempFile2 = File.createTempFile("linshare-test-up", ".tmp");
		List<String> l = Lists.newArrayList();
		IOUtils.transferTo(stream2, tempFile);
		IOUtils.transferTo(stream1, tempFile2);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName2, comment2, false, null);
		Assert.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) != null);
		aDocumentEntry.getDocument().setSha256sum(null);
		documentRepository.update(aDocumentEntry.getDocument());
		l = shaSumBatch.getAll(batchRunContext);
		int i;
		ResultContext c;
		for (i = 0; i < l.size(); i++) {
			c = shaSumBatch.execute(batchRunContext, l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			Document doc = documentRepository.findByUuid(l.get(i));
			Assert.assertEquals("0679aeee7c0c5c4a9a4322326f0243c29025a696a4c2436758470d30ec9488a0", doc.getSha256sum());
			Assert.assertEquals("b09efecaccbf880c60539f04659489df54698afd", doc.getSha1sum());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	private void createFunctionalities() throws IllegalArgumentException, BusinessException {
		Integer value = 1;
		ArrayList<Functionality> functionalities = new ArrayList<Functionality>();
		functionalities.add(
				new UnitValueFunctionality("MIME_TYPE",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);
		functionalities.add(
				new UnitValueFunctionality("ANTIVIRUS",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);
		functionalities.add(
				new UnitValueFunctionality("ENCIPHERMENT",
					true,
					new Policy(Policies.ALLOWED, true),
					new Policy(Policies.ALLOWED, true),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);
		functionalities.add(
				new StringValueFunctionality("TIME_STAMPING",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					""
				)
		);
		functionalities.add(
				new UnitValueFunctionality("DOCUMENT_EXPIRATION",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new TimeUnitClass(TimeUnit.DAY)
				)
		);
		for (Functionality functionality : functionalities) {
			functionalityRepository.create(functionality);
			jane.getDomain().addFunctionality(functionality);
		}
	}
}

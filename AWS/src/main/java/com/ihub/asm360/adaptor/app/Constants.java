/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app;

/**
 * The Class Constants.
 */
public class Constants {

	/**
	 * Instantiates a new constants.
	 */
	private Constants() {
		// Prevent instantiation
	}

	/**
	 * The Constants Interface Database.
	 */
	public interface Database {

		/** The Constant DATABASE_DRIVER_DATASOURCE_CLAZZ. */
		public static final String DATABASE_DRIVER_DATASOURCE_CLAZZ = "db.driver.datasource.clazz";

		/** The Constant DATABASE_DRIVER_CLAZZ. */
		public static final String DATABASE_DRIVER_CLAZZ = "db.driver.clazz";

		/** The Constant DATABASE_CONNECTION_TEST_QUERY. */
		public static final String DATABASE_CONNECTION_TEST_QUERY = "db.connection.test.query";

		/** The Constant DATABASE_MAX_POOL_SIZE. */
		public static final String DATABASE_MAX_POOL_SIZE = "db.max.pool.size";

		/** The Constant DATABASE_URL. */
		public static final String DATABASE_URL = "db.url";

		/** The Constant DATABASE_USERNAME. */
		public static final String DATABASE_USERNAME = "db.username";

		/** The Constant DATABASE_PWD. */
		public static final String DATABASE_PWD = "db.pwd";

	}

	/**
	 * Constants related to Hibernate ORM.
	 */
	public interface Hibernate {

		/** The Constant HIBERNATE_DIALECT. */
		public static final String HIBERNATE_DIALECT = "hibernate.dialect";

		/** The Constant HIBERNATE_DIALECT_TEST. */
		public static final String HIBERNATE_DIALECT_TEST = "hibernate.dialect.test";

		/** The Constant HIBERNATE_SHOW_SQL. */
		public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";

		/** The Constant HIBERNATE_HBM2DDL. */
		public static final String HIBERNATE_HBM2DDL = "hibernate.hbm2ddl.auto";

		/** The Constant HIBERNATE_FORMAT_SQL. */
		public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";

		/** The Constant HIBERNATE_SHOULD_VALIDATE. */
		public static final String HIBERNATE_SHOULD_VALIDATE = "hibernate.should.validate";

		/** The Constant HIBERNATE_SHOULD_VALIDATE. */
		public static final String HIBERNATE_NAMING_STRATEGY = "spring.jpa.hibernate.naming-strategy";

		/** Allows Hibernate to generate SQL optimized for a particular DBMS. */
		public static final String HIBERNATE_INNODB_DIALECT = "spring.jpa.properties.hibernate.dialect";
	}

	/**
	 * The Interface Strings.
	 */
	public interface Strings {

		/** The Constant TRUE. */
		public static final String TRUE = "true";

		/** The Constant FALSE. */
		public static final String FALSE = "false";

		/** The Constant YES. */
		public static final String YES = "YES";

		/** The Constant NO. */
		public static final String NO = "NO";

	}

	/**
	 * The Interface ApiVersion.
	 */
	public interface ApiVersion {

		/** The Constant V1. */
		public static final String V1 = "X-API-VERSION=v1";
	}

	/**
	 * The Interface RequestHeader.
	 */
	public interface RequestHeader {

		/** The Constant API_TOKEN. */
		public static final String API_TOKEN = "X-API-TOKEN";

		/** The Constant SOURCE_SYSTEM_EAG_SERVICE_NOW. */
		public static final String SOURCE_SYSTEM_EAG_SERVICE_NOW = "EAG_SERVICE_NOW";

		/** The Constant SOURCE_SYSTEM_EAG_DYNATRACE. */
		public static final String SOURCE_SYSTEM_EAG_DYNATRACE = "EAG_DYNATRACE";
	}

	/**
	 * The Interface Profile.
	 */
	public interface Profile {

		/**
		 * The Interface Common.
		 */
		public interface Common {

			/** The Constant PROFILE_NAME. */
			public static final String PROFILE_NAME = "COMMON";

			/** The Constant APPLICATION_DETAIL_COREDEF_URL. */
			public static final String APPLICATION_DETAIL_COREDEF_URL = "APPLICATION_DETAIL_COREDEF_URL";
		}

		/**
		 * The Interface EAGDynatrace.
		 */
		public interface EAGDynatrace {

			/** The Constant PROFILE_NAME. */
			public static final String PROFILE_NAME = "EAG_DYNATRACE";

			/** The Constant IS_PULL_ACTIVE. */
			public static final String IS_PULL_ACTIVE = "IS_PULL_ACTIVE";

			/** The Constant INCIDENT_DETAIL_URL. */
			public static final String INCIDENT_DETAIL_URL = "INCIDENT_DETAIL_URL";

			/** The Constant API_TOKEN. */
			public static final String API_TOKEN = "API_TOKEN";

			/** The Constant INCIDENT_PULL_URL. */
			public static final String INCIDENT_PULL_URL = "INCIDENT_PULL_URL";

		}

		/**
		 * The Interface EAGServiceNow.
		 */
		public interface EAGServiceNow {

			/** The Constant PROFILE_NAME. */
			public static final String PROFILE_NAME = "EAG_SERVICENOW";

			/** The Constant IS_PULL_ACTIVE. */
			public static final String IS_PULL_ACTIVE = "IS_PULL_ACTIVE";

			/** The Constant INCIDENT_DETAIL_URL. */
			public static final String INCIDENT_DETAIL_URL = "INCIDENT_DETAIL_URL";

			/** The Constant DATA_MAPPER_CALLER_ID. */
			public static final String DATA_MAPPER_CALLER_ID = "CALLER_ID";

			/** The Constant INCIDENT_PULL_URL. */
			public static final String INCIDENT_PULL_URL = "INCIDENT_PULL_URL";

			/** The Constant INCIDENT_PULL_USERNAME. */
			public static final String INCIDENT_PULL_USERNAME = "INCIDENT_PULL_USERNAME";

			/** The Constant INCIDENT_PULL_PASSWORD. */
			public static final String INCIDENT_PULL_PASSWORD = "INCIDENT_PULL_PASSWORD";
			
			public static final String PULL_INTERVAL_SECONDS = "PULL_INTERVAL_SECONDS";

		}

		/**
		 * The Interface WileyServiceNow.
		 */
		public interface WileyServiceNow {

			/** The Constant PROFILE_NAME. */
			public static final String PROFILE_NAME = "WILEY_SERVICENOW";

			/** The Constant IS_PULL_ACTIVE. */
			public static final String IS_PULL_ACTIVE = "IS_PULL_ACTIVE";

			/** The Constant INCIDENT_DETAIL_URL. */
			public static final String INCIDENT_DETAIL_URL = "INCIDENT_DETAIL_URL";

			/** The Constant API_TOKEN. */
			public static final String API_TOKEN = "API_TOKEN";

		}
		
		/**
		 * The Interface AwsServiceNow.
		 */
		public interface AwsServiceNow{
			
			/** The Constant PROFILE_NAME. */
			public static final String PROFILE_NAME = "AWS_SERVICENOW";
			
			/** The Constant IS_PULL_ACTIVE. */
			public static final String IS_PULL_ACTIVE = "IS_PULL_ACTIVE";

			/** The Constant INCIDENT_DETAIL_URL. */
			public static final String INCIDENT_DETAIL_URL = "INCIDENT_DETAIL_URL";

			/** The Constant API_TOKEN. */
			public static final String API_TOKEN = "API_TOKEN";
		}

	}

}

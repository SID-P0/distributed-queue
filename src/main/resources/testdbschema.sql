-- Table for Jobs
-- This table stores the high-level job information.
CREATE TABLE Jobs (
    job_id VARCHAR(255) PRIMARY KEY, -- Unique identifier for the job
    job_name VARCHAR(255) NOT NULL,
    job_action VARCHAR(50) NOT NULL, -- Stores the JobAction enum as a string (e.g., 'JOB_NEW', 'JOB_UPDATE')
    job_type VARCHAR(50) NOT NULL,   -- Stores the JobType enum as a string (e.g., 'EMAIL_JOB', 'DATA_POPULATION_JOB')
    job_description TEXT,            -- Detailed description of the job
    created_by VARCHAR(255) NOT NULL,
    creation_time_stamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table for TaskMetadata
-- This table stores common metadata for all tasks.
-- Each row here represents a single task instance within a job.
CREATE TABLE TaskMetadata (
    task_id VARCHAR(255) PRIMARY KEY, -- Unique identifier for the task
    job_id VARCHAR(255) NOT NULL,     -- Foreign key to the Jobs table
    task_action VARCHAR(50) NOT NULL, -- Stores the TaskAction enum as a string (e.g., 'TASK_CREATE', 'TASK_SUCCESS')
    task_description TEXT,
    task_last_modified_by VARCHAR(255),
    task_last_modified_time_stamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Add a column to link to the specific payload table
    payload_type VARCHAR(50) NOT NULL, -- e.g., 'EMAIL', 'PRIORITY_FLOW', 'DATA_POPULATION'
    payload_id VARCHAR(255) UNIQUE,    -- Foreign key to the specific payload table (EmailPayloads, PriorityFlowPayloads, DataPopulationPayloads)
                                       -- This column will be NULL for payload types that don't exist yet, or if a task doesn't have a specific payload.
                                       -- We use UNIQUE here because a payload_id should only link to one task_id.

    FOREIGN KEY (job_id) REFERENCES Jobs(job_id)
    -- Note: The foreign key constraint for payload_id will be handled by the application logic
    -- or by deferred constraints if your RDBMS supports them, as it links to one of several tables.
    -- A common pattern is to manage this relationship in the application layer.
);

-- Table for EmailPayloads
-- Stores specific data for email-related tasks.
CREATE TABLE EmailPayloads (
    email_payload_id VARCHAR(255) PRIMARY KEY, -- Unique ID for this specific email payload instance
    task_id VARCHAR(255) UNIQUE NOT NULL,      -- Foreign key to TaskMetadata, ensuring 1:1 relationship
    unique_mail_id VARCHAR(255),
    sender_id VARCHAR(255) NOT NULL,
    title_heading VARCHAR(255),
    message_body TEXT,
    mail_time_stamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (task_id) REFERENCES TaskMetadata(task_id)
);

-- Table for EmailRecipient (for repeated recipient_id in EmailPayload)
-- This is a many-to-one relationship: one email payload can have many recipients.
CREATE TABLE EmailRecipients (
    recipient_entry_id PRIMARY KEY, -- ID for each recipient entry
    email_payload_id VARCHAR(255) NOT NULL, -- Foreign key to EmailPayloads
    recipient_address VARCHAR(255) NOT NULL, -- The actual recipient email address

    FOREIGN KEY (email_payload_id) REFERENCES EmailPayloads(email_payload_id)
);

-- Table for PriorityFlowPayloads
-- Stores specific data for priority flow tasks.
CREATE TABLE PriorityFlowPayloads (
    priority_flow_payload_id VARCHAR(255) PRIMARY KEY, -- Unique ID for this specific priority flow payload instance
    task_id VARCHAR(255) UNIQUE NOT NULL,             -- Foreign key to TaskMetadata, ensuring 1:1 relationship
    prio_f_id VARCHAR(255) NOT NULL,
    prio_f_rank INT NOT NULL,
    prio_f_name VARCHAR(255) NOT NULL,

    FOREIGN KEY (task_id) REFERENCES TaskMetadata(task_id)
);

-- Table for DataPopulationPayloads
-- Stores specific data for data population tasks.
CREATE TABLE DataPopulationPayloads (
    data_population_payload_id VARCHAR(255) PRIMARY KEY, -- Unique ID for this specific data population payload instance
    task_id VARCHAR(255) UNIQUE NOT NULL,               -- Foreign key to TaskMetadata, ensuring 1:1 relationship
    source_system VARCHAR(255) NOT NULL,
    target_entity VARCHAR(255) NOT NULL,
    filter_criteria TEXT,

    FOREIGN KEY (task_id) REFERENCES TaskMetadata(task_id)
);
BEGIN;

-- Table for high-level job containers.
CREATE TABLE IF NOT EXISTS jobs (
    -- The backend MUST provide a value for job_id on INSERT.
    job_id UUID PRIMARY KEY,

    job_name VARCHAR(255) NOT NULL,
    job_action VARCHAR(50) NOT NULL CHECK (job_action IN (
        'JOB_NEW', 'JOB_UPDATE', 'JOB_DELETE', 'JOB_AWAITING_ACTION',
        'JOB_COMPLETED', 'JOB_FAILURE', 'JOB_STATUS_UNSPECIFIED'
    )),
    job_description TEXT,
    created_by VARCHAR(255) NOT NULL,
    creation_timestamp TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Base table for all tasks, representing the common TaskMetadata.
CREATE TABLE IF NOT EXISTS tasks (
    -- The backend MUST provide a value for task_id on INSERT.
    task_id UUID PRIMARY KEY,

    -- This foreign key (job_id) must also be provided by the backend.
    job_id UUID NOT NULL REFERENCES jobs(job_id) ON DELETE CASCADE,

    -- Discriminator column to identify the payload type.
    task_type VARCHAR(50) NOT NULL CHECK (task_type IN (
        'EMAIL_JOB', 'DATA_POPULATION_JOB', 'PRIORITY_FLOW_JOB', 'JOB_TYPE_UNSPECIFIED'
    )),

    -- Fields from the TaskMetadata proto message
    task_action VARCHAR(50) NOT NULL CHECK (task_action IN (
        'TASK_CREATE', 'TASK_UPDATE', 'TASK_DELETE', 'TASK_SEND', 'TASK_RECEIVE',
        'TASK_AWAITING_INPUT', 'TASK_SUCCESS', 'TASK_FAILURE', 'TASK_EXECUTION_STATUS_UNSPECIFIED'
    )),
    task_description TEXT,
    task_last_modified_by VARCHAR(255) NOT NULL,
    task_last_modified_timestamp TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Table specifically for EmailPayload data.
CREATE TABLE IF NOT EXISTS email_payloads (
    -- This task_id is both a primary key and a foreign key.
    task_id UUID PRIMARY KEY REFERENCES tasks(task_id) ON DELETE CASCADE,

    unique_mail_id VARCHAR(255) NOT NULL,
    sender_id VARCHAR(255) NOT NULL,
    recipient_ids TEXT[] NOT NULL, -- PostgreSQL array for 'repeated' fields
    title_heading VARCHAR(512) NOT NULL,
    message_body TEXT,
    mail_timestamp TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Table specifically for PriorityFlowPayload data.
CREATE TABLE IF NOT EXISTS priority_flow_payloads (
    task_id UUID PRIMARY KEY REFERENCES tasks(task_id) ON DELETE CASCADE,
    prio_f_id VARCHAR(255) NOT NULL,
    prio_f_rank INT NOT NULL,
    prio_f_name VARCHAR(255) NOT NULL
);

-- Table specifically for DataPopulationPayload data.
CREATE TABLE IF NOT EXISTS data_population_payloads (
    task_id UUID PRIMARY KEY REFERENCES tasks(task_id) ON DELETE CASCADE,
    source_system VARCHAR(255) NOT NULL,
    target_entity VARCHAR(255) NOT NULL,
    filter_criteria TEXT,
    payload_data JSONB
);

-- Indexes to optimize JOINs and lookups.
CREATE INDEX IF NOT EXISTS idx_tasks_job_id ON tasks(job_id);
CREATE INDEX IF NOT EXISTS idx_tasks_task_type ON tasks(task_type);

COMMIT;
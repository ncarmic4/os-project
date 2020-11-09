# Job Metrics
* Job ID: ID number of the job.
* CPU ID: ID number of the cpu that ran the job.
* Waiting Time: Time in milliseconds that the job was waiting to be run.
* Completion Time: Time in milliseconds that the job took to complete.
* I/O Processes: Number of I/O processes that the job makes during its lifespan.
* MMU RAM % Used: At the time the job is run, percentage of RAM used by all jobs.
* Job RAM % Used: The percentage of RAM used by this job.
* Job Cache % Used: The percentage of cache of the assigned cpu used by this job.

# CPU Metrics
* CPU ID: ID number of the cpu that ran the job.
* Completion Time: Time in milliseconds that the cpu took to complete all assigned jobs.
* I/O Processes: Number of I/O processes that the cpu makes during its lifespan.
* Number of Jobs: Total number of jobs the CPU runs during its lifespan.
* % of Jobs: Percentage of jobs the CPU runs out of the total job pool.
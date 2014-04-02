Solr commands for eventium cores(event tracking DB).
Started: [2014-03-01]

TODO: consider security (auth-n).

1. To create DB:
1.1. Copy dir ddl/eventium into cores storage(e.g. /var/lib/solr_cores).

2. To create runtime:
2.1. Download current version of solr (for now 4.7.0).

2.2. Copy dir runtimes/solr/[OS]/solr_etr to runtimes folder (e.g. /var/lib/runtimes/)
Replace values in _env to your env specific.

3. To start solr:
3.1. Go to runtimes dir. 
3.2. Start using start_solr. 

4. To load initial data:
4.1. Go to runtimes.
4.2. Start solr (start_solr).
4.3. Launch data import (load_data).


Java7 DB
Inception: [2014-01-14]


NOTE: if you use any native java DB commands, run _env first to init env.

1. Start DB: 
./startdb.sh

2. Stop DB: 
./stopdb.sh

3. To got to derby client, call: 
./clij.sh
Then connect to ETR schema(don't forget to create it first, see configs/javadb): 

connect 'jdbc:derby://localhost:1527/eventium;user=etracker;password=shmetracker';

Sample select: 
select id,category_code,severity,process_id,created from events order by created desc;

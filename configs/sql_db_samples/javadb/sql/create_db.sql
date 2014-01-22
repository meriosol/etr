-- Create new DB with name eventium.
--  In derby ij client:
CONNECT 'jdbc:derby://localhost:1527/eventium;create=true;user=etracker;password=shmetracker';

-- Create new user:
CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user.etracker', 'shmetracker');
CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.fullAccessUsers', 'etracker');


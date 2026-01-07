ALTER TABLE event
ADD CONSTRAINT check_event_dates CHECK (end_date > start_date);
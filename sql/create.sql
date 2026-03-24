CREATE TABLE company (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    inn TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE stop (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    city TEXT,
    address TEXT
);

CREATE TABLE route (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
    route_number TEXT NOT NULL,
    name TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_route_company_number UNIQUE (company_id, route_number)
);

CREATE TABLE route_stop (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES route(id) ON DELETE CASCADE,
    seq INTEGER NOT NULL,
    stop_id BIGINT NOT NULL REFERENCES stop(id) ON DELETE RESTRICT,
    dwell_min INTEGER,
    CONSTRAINT uq_route_stop_seq UNIQUE (route_id, seq),
    CONSTRAINT chk_route_stop_seq_positive CHECK (seq > 0),
    CONSTRAINT chk_route_stop_dwell_nonneg CHECK (dwell_min IS NULL OR dwell_min >= 0)
);

CREATE TABLE route_fare (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES route(id) ON DELETE CASCADE,
    from_route_stop_id BIGINT NOT NULL REFERENCES route_stop(id) ON DELETE RESTRICT,
    to_route_stop_id BIGINT NOT NULL REFERENCES route_stop(id) ON DELETE RESTRICT,
    price NUMERIC(10,2) NOT NULL,
    CONSTRAINT uq_route_fare UNIQUE (route_id, from_route_stop_id, to_route_stop_id),
    CONSTRAINT chk_route_fare_price_positive CHECK (price >= 0),
    CONSTRAINT chk_route_fare_from_to_diff CHECK (from_route_stop_id <> to_route_stop_id)
);

CREATE TABLE trip (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES route(id) ON DELETE RESTRICT,
    departure_at TIMESTAMPTZ NOT NULL,
    capacity INTEGER NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_trip_capacity_positive CHECK (capacity > 0)
);

CREATE TABLE trip_stop_time (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
    route_stop_id BIGINT NOT NULL REFERENCES route_stop(id) ON DELETE RESTRICT,
    arrival_at TIMESTAMPTZ,
    departure_at TIMESTAMPTZ,
    CONSTRAINT uq_trip_stop_time UNIQUE (trip_id, route_stop_id)
);

CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    address TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES client(id) ON DELETE RESTRICT,
    trip_id BIGINT NOT NULL REFERENCES trip(id) ON DELETE RESTRICT,
    from_route_stop_id BIGINT NOT NULL REFERENCES route_stop(id) ON DELETE RESTRICT,
    to_route_stop_id BIGINT NOT NULL REFERENCES route_stop(id) ON DELETE RESTRICT,
    price NUMERIC(10,2) NOT NULL,
    status TEXT NOT NULL,
    payment_status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    paid_at TIMESTAMPTZ,
    canceled_at TIMESTAMPTZ,
    canceled_reason TEXT,
    CONSTRAINT chk_orders_price_nonneg CHECK (price >= 0),
    CONSTRAINT chk_orders_from_to_diff CHECK (from_route_stop_id <> to_route_stop_id)
);
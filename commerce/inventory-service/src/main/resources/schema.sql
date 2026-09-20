CREATE TABLE IF NOT EXISTS inventories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT UNIQUE NOT NULL,
    quantity INTEGER NOT NULL,
    reserved_quantity INTEGER NOT NULL,
    available_quantity INTEGER NOT NULL,
    version BIGINT NOT NULL
);
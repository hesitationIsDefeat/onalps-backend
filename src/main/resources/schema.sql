--DROP TABLE IF EXISTS marketplace CASCADE;
--DROP TABLE IF EXISTS brand CASCADE;
--DROP TABLE IF EXISTS ai_infu CASCADE;
DROP TABLE IF EXISTS post CASCADE;

CREATE TABLE IF NOT EXISTS marketplace (
    id UUID PRIMARY KEY, -- @GeneratedValue(strategy = GenerationType.UUID)

    public_id VARCHAR(255) NOT NULL UNIQUE, -- @Column(unique = true, nullable = false)

    name VARCHAR(255) NOT NULL, -- @Column(nullable = false)

    hashed_secret_key VARCHAR(255) NOT NULL, -- @Column(nullable = false)

    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- private boolean isActive = true

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- @CreationTimestamp

    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- @UpdateTimestamp
);

CREATE TABLE IF NOT EXISTS brand (
    id UUID PRIMARY KEY, -- @GeneratedValue(strategy = GenerationType.UUID)

    public_id VARCHAR(255) NOT NULL UNIQUE, -- unique external identifier

    marketplace_id UUID NOT NULL, -- foreign key to marketplace.id

    name VARCHAR(255) NOT NULL, -- brand name

    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- default true

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- @CreationTimestamp

    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- @UpdateTimestamp

    CONSTRAINT fk_brand_marketplace FOREIGN KEY (marketplace_id) REFERENCES marketplace (id)
);

CREATE TABLE IF NOT EXISTS ai_infu (
    id UUID PRIMARY KEY, -- @GeneratedValue(strategy = GenerationType.UUID)

    public_id VARCHAR(255) NOT NULL UNIQUE, -- external/public identifier

    marketplace_id UUID NOT NULL, -- FK -> marketplace.id
    brand_id UUID NOT NULL,       -- FK -> brand.id

    image_url TEXT NOT NULL DEFAULT '', -- URL to the image
    name VARCHAR(255) NOT NULL,      -- name of the AiInfu entity
    prompt TEXT NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- default true
    state INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- auto set on insert
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- auto set on update

    CONSTRAINT fk_ai_infu_marketplace FOREIGN KEY (marketplace_id) REFERENCES marketplace (id),
    CONSTRAINT fk_ai_infu_brand FOREIGN KEY (brand_id) REFERENCES brand (id)
);

CREATE TABLE IF NOT EXISTS post (
    id UUID PRIMARY KEY,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    marketplace_id UUID NOT NULL,
    brand_id UUID NOT NULL,
    ai_infu_id UUID NOT NULL,
    image_url VARCHAR(2048) NOT NULL,
    product_url VARCHAR(2048) NOT NULL,
    prompt VARCHAR(2048) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_marketplace FOREIGN KEY (marketplace_id) REFERENCES marketplace(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_brand FOREIGN KEY (brand_id) REFERENCES brand(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_ai_infu FOREIGN KEY (ai_infu_id) REFERENCES ai_infu(id) ON DELETE CASCADE
);



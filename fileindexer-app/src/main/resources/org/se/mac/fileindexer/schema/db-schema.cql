CREATE TABLE IF NOT EXISTS logicalfilerepository (
    id UUID PRIMARY KEY,
    updated_date date,
    scanTime bigint,
    deviceId UUID,
    devicePath text,
    properties map<text,text>
    );

CREATE TABLE IF NOT EXISTS filehashrepository (
    id UUID PRIMARY KEY,
    updated_date date,
    scanTime bigint,
    deviceId UUID,
    devicePath text,
    hash text,
    );

CREATE TABLE IF NOT EXISTS logicaldevice (
    deviceId UUID PRIMARY KEY,
    title text,
    created_date date
    )
    WITH comment = 'Q1. Find devices by deviceId';

CREATE TABLE IF NOT EXISTS logicaluri (
    uri text,
    deviceId UUID,
    PRIMARY KEY(deviceId, uri)
    )
    WITH comment = 'Q2. Find uris by deviceId and uri';

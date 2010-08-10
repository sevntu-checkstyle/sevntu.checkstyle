package com.revere.doc.resource;

// TODO  this is not a resource anymore, but:
// - OpenedResource
// - Connection
// - ???

public interface IDocumentResource {

	public String getMediaType();

	public long getLastModified();

	public void close();

}

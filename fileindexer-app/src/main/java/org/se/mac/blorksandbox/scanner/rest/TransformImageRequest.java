package org.se.mac.blorksandbox.scanner.rest;

public record TransformImageRequest(String imageId, int imageWidth, int imageHeight, ImageTransformDefinition transformation) {
}

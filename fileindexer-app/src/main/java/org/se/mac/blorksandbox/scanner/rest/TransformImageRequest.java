package org.se.mac.blorksandbox.scanner.rest;

/**
 * Data carrier when requesting an image transformation.
 *
 * @param imageId        ID of the image to transform
 * @param imageWidth     New image width
 * @param imageHeight    New image height
 * @param transformation Definition of the transformation methodology
 */
public record TransformImageRequest(String imageId, int imageWidth, int imageHeight,
                                    ImageTransformDefinition transformation) {
}

# Regular Grid Acceleration - Unit Tests Guide

## Overview

This document explains how to run the automated tests for the Regular Grid acceleration feature implemented in the ISE5786 Mini-Project 2 ray-tracing system.

## Test Files Added

### 1. **AABBTests.java** (`unittests/primitives/`)
Tests the AABB (Axis-Aligned Bounding Box) utility class.

**Tests include:**
- Ray-AABB intersection using the slab method
- Ray starting inside box, missing box, parallel to box
- AABB union of overlapping/non-overlapping boxes
- AABB validity checking
- Degenerate boxes (min == max)

**To run:**
```bash
mvn test -Dtest=AABBTests
```

### 2. **GeometryAABBTests.java** (`unittests/geometries/impl/`)
Tests that each geometry correctly computes its bounding box.

**Tests include:**
- Sphere AABB computation (at origin, arbitrary center)
- Triangle AABB computation
- Polygon (quad) AABB computation
- Infinite geometries (Plane, Tube) correctly return null
- AABB validity for each finite geometry

**To run:**
```bash
mvn test -Dtest=GeometryAABBTests
```

### 3. **RegularGridTests.java** (`unittests/geometries/impl/`)
Core tests for the Regular Grid acceleration structure.

**Tests include:**
- Grid construction with single/multiple/empty geometries
- Grid construction with mixed finite and infinite geometries
- Voxel assignment verification
- Sparse voxel storage (only non-empty voxels stored)
- Intersection calculation via grid traversal
- Avoidance of duplicate geometry testing per ray
- Automatic resolution calculation with density parameter
- Resolution clamping to min/max bounds
- Grid configuration API

**To run:**
```bash
mvn test -Dtest=RegularGridTests
```

### 4. **RegularGridIntegrationTests.java** (`unittests/geometries/impl/`)
Integration tests comparing baseline (no grid) vs grid-accelerated rendering.

**Tests include:**
- Intersection results match with/without grid
- Closest intersection is identical
- Disabling grid reverts to baseline behavior
- Rays starting inside geometries handled correctly
- Small and large sparse scenes
- Rays parallel and diagonal to axes
- Grid configuration variations
- Multiple enable/disable cycles

**To run:**
```bash
mvn test -Dtest=RegularGridIntegrationTests
```

### 5. **RegularGridRenderingCorrectnessTests.java** (`unittests/renderer/`)
Performance and rendering correctness tests.

**Tests include:**
- Ray-geometry intersections match between baseline and grid
- Performance benchmark (informational output)
- Toggle grid on/off without breaking results
- Stress test with many objects (7×7×7 = 343 spheres)
- Grid stability over repeated queries
- Empty scene handling
- Different grid configurations produce consistent results

**To run:**
```bash
mvn test -Dtest=RegularGridRenderingCorrectnessTests
```

## Running All Regular Grid Tests

To run all Regular Grid tests at once:

```bash
mvn test -Dtest=*RegularGrid*
```

Or to run AABBTests as well:

```bash
mvn test -Dtest=*RegularGrid*,*AABB*
```

## Running Tests from IDE

If using IntelliJ IDEA or Eclipse:
1. Right-click on the test file in the Project view
2. Select "Run '...'Tests" or press `Ctrl+Shift+F10` (IntelliJ)
3. To run a specific test method, click on the method name and run

## Test Configuration Reference

Most tests use the default grid configuration:
```java
new RegularGrid.Config(density, minResolution, maxResolution)
```

Common configurations:
- **Coarse grid:** `new RegularGrid.Config(0.5, 1, 30)`
- **Medium grid:** `new RegularGrid.Config(1.0, 2, 50)`
- **Fine grid:** `new RegularGrid.Config(2.0, 10, 100)`

## API Usage Examples from Tests

### Enable Regular Grid

```java
Scene scene = new Scene("MyScene");
// ... add geometries ...
scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));
```

### Disable Regular Grid

```java
scene.geometries.disableRegularGrid();
```

### Test Ray Intersections

```java
Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
var intersections = scene.geometries.calcIntersections(ray);

if (intersections != null) {
    System.out.println("Found " + intersections.size() + " intersections");
} else {
    System.out.println("No intersections");
}
```

## Expected Test Results

All tests should **PASS** when:
1. Regular Grid is correctly implemented with 3DDDA traversal
2. AABB computation is correct for all finite geometries
3. Infinite geometries are handled separately
4. Duplicate geometry testing is avoided per ray
5. Grid configuration parameters work as expected

If tests fail, check:
- AABB implementation for correctness
- 3DDDA traversal logic for off-by-one errors
- Voxel index calculation and clamping
- Identity-based set for duplicate avoidance

## Performance Baseline

On a typical scene with 25 spheres (5×5 grid) + 2 triangles:
- **Baseline (no grid):** ~80-150ms for 200 ray intersection queries
- **With grid:** ~40-100ms for same queries (configuration-dependent)
- **Speedup expected:** 1.5-2.5x on complex scenes

Actual speedup depends on:
- Number and distribution of geometries
- Grid resolution (density parameter)
- Ray distribution in the scene

## Continuous Integration

These tests are designed to be CI-friendly:
- No external file I/O (except optional System.out logging)
- No randomness (deterministic results)
- Timeout-safe (no infinite loops)
- Parallel-safe (tests use local scene instances)

## Further Testing

To add custom tests, create new methods in the existing test classes or create a new test class:

```java
@Test
void myCustomTest() {
    // ... test code ...
}
```

Follow the naming convention: `test*` for test methods, `*Tests` for test class names.

## Troubleshooting

### Tests compile but fail to run
- Ensure JUnit 5 is in your classpath (pom.xml should have junit-jupiter dependency)

### "Cannot find symbol" errors
- Rebuild the project: `mvn clean compile`

### Tests timeout
- Check for infinite loops in your 3DDDA implementation
- Verify voxel traversal termination conditions

### Performance tests show high grid overhead
- Grid has overhead on small scenes; benefits appear with 50+ geometries
- Adjust density parameter for optimal performance

## Questions?

Refer to the implementation comments in:
- `RegularGrid.java` - main acceleration structure
- `AABB.java` - bounding box utility
- Test files - usage examples and expected behavior

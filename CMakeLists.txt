cmake_minimum_required(VERSION 2.8)
project(nlopt4j)

enable_language(C)
enable_language(CXX)
enable_testing()

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake")

find_package(JNI REQUIRED)
find_package(NLOPT REQUIRED)

if (CMAKE_COMPILER_IS_GNUCXX)
  set(CMAKE_CXX_FLAGS "-O2 -g3 -Wall -Wno-sign-compare -std=c++11")
  set(CMAKE_CXX_FLAGS_RELEASE "-O2 -g3 -Wall -Wno-sign-compare -std=c++11")
  set(CMAKE_CXX_FLAGS_DEBUG "-O2 -g3 -Wall -Wno-sign-compare -std=c++11")
elseif (MSVC)
  set(CMAKE_C_FLAGS "/GF /nologo")
  set(CMAKE_C_FLAGS_DEBUG "/Zi /Od /D_DEBUG /RTC1 /MDd")
  set(CMAKE_C_FLAGS_RELEASE "/DNDEBUG /O2 /Oy /GL /Gy /MD")
  set(CMAKE_C_FLAGS_RELWITHDEBINFO "/DNDEBUG /Zi /O2 /Oy- /GL /Gy /MD")
  set(CMAKE_C_FLAGS_MINSIZEREL "/DNDEBUG /O1 /Oy /GL /Gy /MD")    
    
  set(CMAKE_CXX_FLAGS "/EHsc /GF /nologo")
  set(CMAKE_CXX_FLAGS_DEBUG "/Zi /Od /D_DEBUG /RTC1 /MDd /Ob0 /GS")
  set(CMAKE_CXX_FLAGS_RELEASE "/DNDEBUG /O2 /Oy /GL /Gy /MD")
  set(CMAKE_CXX_FLAGS_RELWITHDEBINFO "/DNDEBUG /Zi /O2 /Oy- /GL /Gy /MD")
  set(CMAKE_CXX_FLAGS_MINSIZEREL "/DNDEBUG /O1 /Oy /GL /Gy /MD")  
    
  SET(CMAKE_EXE_LINKER_FLAGS "/DYNAMICBASE /NXCOMPAT /LARGEADDRESSAWARE")

  SET(CMAKE_EXE_LINKER_FLAGS_DEBUG "/DEBUG")
  SET(CMAKE_EXE_LINKER_FLAGS_RELEASE "/RELEASE /LTCG /OPT:REF /OPT:ICF /INCREMENTAL:NO")
  SET(CMAKE_EXE_LINKER_FLAGS_RELWITHDEBINFO "/DEBUG /RELEASE /LTCG /OPT:REF /OPT:ICF /INCREMENTAL:NO /DEBUGTYPE:cv,fixup")
  SET(CMAKE_EXE_LINKER_FLAGS_MINSIZEREL "/RELEASE /LTCG /OPT:REF /OPT:ICF /INCREMENTAL:NO")

  # Same linker settings for DLL as EXE
  SET(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS}")
  SET(CMAKE_SHARED_LINKER_FLAGS_DEBUG "${CMAKE_EXE_LINKER_FLAGS_DEBUG}")
  SET(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_EXE_LINKER_FLAGS_RELEASE}")
  SET(CMAKE_SHARED_LINKER_FLAGS_RELWITHDEBINFO "${CMAKE_EXE_LINKER_FLAGS_RELWITHDEBINFO}")
  SET(CMAKE_SHARED_LINKER_FLAGS_MINSIZEREL "${CMAKE_EXE_LINKER_FLAGS_MINSIZEREL}")

  # Disable signed/unsigned int conversion warnings.
  add_definitions("/wd4018")
  # Disable warning about using struct/class for the same symobl.
  add_definitions("/wd4099")
  # Disable warning about the insecurity of using "std::copy".
  add_definitions("/wd4996")
  # Disable performance warning about int-to-bool conversion.
  add_definitions("/wd4800")
endif()

include_directories("${PROJECT_SOURCE_DIR}/include")
include_directories("${JNI_INCLUDE_DIRS}")
include_directories("${NLOPT_INCLUDE_DIRS}")

set(NLOPT_JNI_SRCS src/main/cpp/nlopt_jni.cpp)
file(GLOB NLOPT_JNI_HEADERS "${PROJECT_SOURCE_DIR}/include/*.h")

add_library(nlopt4j SHARED ${NLOPT_JNI_HEADERS} ${NLOPT_JNI_SRCS})
target_link_libraries(nlopt4j ${NLOPT_LIBRARIES}) 


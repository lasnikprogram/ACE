# Developer Guide

## Build instruction
### clone repo
#### https
```
git clone --recurse-submodules https://github.com/KuhakuPixel/AceTheGame.git
```
#### ssh
```
git clone --recurse-submodules git@github.com:KuhakuPixel/AceTheGame.git
```
## Project Structure
specific build instruction is provided at the readme of each subproject. 

Folder 				| Description
----------------------------- 	| -----------------------------------------------
[ACE](./ACE/) 			| Memory scanner and editor for linux and android
[Modder](./Modder) 		| Attaching a memory scanner and editor service in the apk itself so you can read/write memory without root
[ATG](./ATG) 	 		| Mobile Apk GUI For Memory scaner and editor (will be similiar like Game Guardian)

## Making Release

### With Docker
- need to have docker installed

#### build the docker image

```
python ./docker_build.py
```

#### Grabbing the build binaries

1. run the image first

```
python ./docker_run_shell.py
```
2. 
running
```
docker ps
```

should give 
(the container ID might  be different, so you need to adjust your arguments)
```
CONTAINER ID   IMAGE                      COMMAND                  CREATED              STATUS              
e46afdd42728   ace_the_game               "bash"                   About a minute ago   Up About a minute   
```
3. copying the folder to your machine

```
sudo docker cp e46afdd42728:/release .
```

now you should have `release` folder in the current directory


### Without Docker
A python script `./make_release.py` is provided to automatically create binary release for each project
(You need to download [NDK](https://developer.android.com/ndk/downloads)

```
python ./make_release.py [PATH_TO_android.toolchain.cmake]
```

example: 
```
python ./make_release.py ~/Android/Sdk/ndk/25.1.8937393/build/cmake/android.toolchain.cmake
```

the script will create `release` folder




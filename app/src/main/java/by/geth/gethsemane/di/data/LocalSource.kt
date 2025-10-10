package by.geth.gethsemane.di.data

import by.geth.gethsemane.data.source.authors.AuthorsInMemoryCacheSource
import by.geth.gethsemane.data.source.authors.BaseAuthorsLocalSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val localSourceModule = module {
    singleOf<BaseAuthorsLocalSource>(::AuthorsInMemoryCacheSource)
}
package com.greer.tornado

import tornadofx.ItemViewModel

class AccountModel(account: Account) : ItemViewModel<Account>(account) {
    val email = bind(Account::emailProperty)
    val username = bind(Account::usernameProperty)
    val password = bind(Account::passwordProperty)
    val phone = bind(Account::phoneProperty)
    val id = bind(Account::idProperty)
}